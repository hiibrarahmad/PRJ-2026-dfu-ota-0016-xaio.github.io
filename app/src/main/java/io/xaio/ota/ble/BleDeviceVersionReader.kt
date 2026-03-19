package io.xaio.ota.ble

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import io.xaio.ota.AppConfig
import io.xaio.ota.model.DeviceVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume

class BleDeviceVersionReader(private val context: Context) {

    private val gson = Gson()

    suspend fun read(deviceAddress: String): Result<DeviceVersion> = withContext(Dispatchers.IO) {
        runCatching {
            checkBluetoothPermission()
            withTimeout(20_000L) {
                suspendCancellableCoroutine { continuation ->
                    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
                    val adapter = bluetoothManager.adapter ?: error("Bluetooth adapter unavailable.")
                    check(adapter.isEnabled) { "Bluetooth is turned off." }

                    val device = adapter.getRemoteDevice(deviceAddress)
                    var gatt: BluetoothGatt? = null

                    val callback = object : BluetoothGattCallback() {
                        val disValues = mutableMapOf<String, String>()
                        var payload: ExtendedVersionPayload? = null

                        override fun onConnectionStateChange(gattConn: BluetoothGatt, status: Int, newState: Int) {
                            if (status != BluetoothGatt.GATT_SUCCESS) {
                                finishWithError(gattConn, "BLE connection failed with status $status")
                                return
                            }

                            when (newState) {
                                BluetoothProfile.STATE_CONNECTED -> gattConn.discoverServices()
                                BluetoothProfile.STATE_DISCONNECTED -> {
                                    if (continuation.isActive) {
                                        continuation.resume(Result.failure(IllegalStateException("Disconnected before version read completed.")))
                                    }
                                    gattConn.close()
                                }
                            }
                        }

                        override fun onServicesDiscovered(gattConn: BluetoothGatt, status: Int) {
                            if (status != BluetoothGatt.GATT_SUCCESS) {
                                finishWithError(gattConn, "BLE service discovery failed with status $status")
                                return
                            }
                            readCharacteristic(gattConn, AppConfig.deviceInfoServiceUuid, AppConfig.firmwareRevisionUuid)
                        }

                        override fun onCharacteristicRead(
                            gattConn: BluetoothGatt,
                            characteristic: BluetoothGattCharacteristic,
                            value: ByteArray,
                            status: Int,
                        ) {
                            handleCharacteristicRead(gattConn, characteristic, value, status)
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onCharacteristicRead(
                            gattConn: BluetoothGatt,
                            characteristic: BluetoothGattCharacteristic,
                            status: Int,
                        ) {
                            handleCharacteristicRead(
                                gattConn,
                                characteristic,
                                characteristic.value ?: ByteArray(0),
                                status,
                            )
                        }

                        private fun handleCharacteristicRead(
                            gattConn: BluetoothGatt,
                            characteristic: BluetoothGattCharacteristic,
                            value: ByteArray,
                            status: Int,
                        ) {
                            if (status != BluetoothGatt.GATT_SUCCESS) {
                                finishWithError(gattConn, "BLE read failed for ${characteristic.uuid} with status $status")
                                return
                            }

                            val text = value.toString(StandardCharsets.UTF_8).trimEnd('\u0000')
                            when (characteristic.uuid) {
                                AppConfig.firmwareRevisionUuid -> {
                                    disValues["firmware"] = text
                                    readCharacteristic(gattConn, AppConfig.deviceInfoServiceUuid, AppConfig.softwareRevisionUuid)
                                }
                                AppConfig.softwareRevisionUuid -> {
                                    disValues["software"] = text
                                    readCharacteristic(gattConn, AppConfig.deviceInfoServiceUuid, AppConfig.hardwareRevisionUuid)
                                }
                                AppConfig.hardwareRevisionUuid -> {
                                    disValues["hardware"] = text
                                    val service = gattConn.getService(AppConfig.versionServiceUuid)
                                    val versionCharacteristic = service?.getCharacteristic(AppConfig.versionCharacteristicUuid)
                                    if (versionCharacteristic == null) {
                                        completeSuccess(gattConn)
                                    } else {
                                        gattConn.readCharacteristic(versionCharacteristic)
                                    }
                                }
                                AppConfig.versionCharacteristicUuid -> {
                                    payload = runCatching { gson.fromJson(text, ExtendedVersionPayload::class.java) }.getOrNull()
                                    completeSuccess(gattConn)
                                }
                                else -> completeSuccess(gattConn)
                            }
                        }

                        private fun readCharacteristic(
                            gattConn: BluetoothGatt,
                            serviceUuid: java.util.UUID,
                            characteristicUuid: java.util.UUID,
                        ) {
                            val service = gattConn.getService(serviceUuid)
                                ?: return finishWithError(gattConn, "Missing BLE service $serviceUuid")
                            val characteristic = service.getCharacteristic(characteristicUuid)
                                ?: return finishWithError(gattConn, "Missing BLE characteristic $characteristicUuid")
                            gattConn.readCharacteristic(characteristic)
                        }

                        private fun completeSuccess(gattConn: BluetoothGatt) {
                            if (!continuation.isActive) {
                                gattConn.close()
                                return
                            }
                            val firmware = payload?.fw ?: disValues["firmware"].orEmpty()
                            val channel = payload?.channel ?: disValues["software"].orEmpty()
                            val hardware = payload?.hw ?: disValues["hardware"].orEmpty()
                            continuation.resume(
                                Result.success(
                                    DeviceVersion(
                                        firmwareRev = firmware,
                                        softwareRev = channel,
                                        hardwareRev = hardware,
                                        versionCode = payload?.code ?: DeviceVersion.fromSemver(firmware),
                                        securityEpoch = payload?.epoch ?: 0,
                                        channel = if (channel.isBlank()) "stable" else channel,
                                    ),
                                ),
                            )
                            gattConn.disconnect()
                            gattConn.close()
                        }

                        private fun finishWithError(gattConn: BluetoothGatt, message: String) {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(IllegalStateException(message)))
                            }
                            gattConn.disconnect()
                            gattConn.close()
                        }
                    }

                    gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        device.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE)
                    } else {
                        device.connectGatt(context, false, callback)
                    }

                    continuation.invokeOnCancellation {
                        gatt?.disconnect()
                        gatt?.close()
                    }
                }.getOrThrow()
            }
        }
    }

    private fun checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT,
        ) == PackageManager.PERMISSION_GRANTED
        check(granted) { "Bluetooth permission is not granted." }
    }

    private data class ExtendedVersionPayload(
        val fw: String? = null,
        val code: Int? = null,
        val epoch: Int? = null,
        val channel: String? = null,
        val hw: String? = null,
    )
}

