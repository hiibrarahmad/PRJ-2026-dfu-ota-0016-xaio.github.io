package io.xaio.ota

import java.util.UUID

object AppConfig {
    val supportedChannels = listOf("stable", "beta", "dev")
    const val otaCachePrefs = "ota_cache"
    const val catalogCacheTtlMs = 60 * 60 * 1000L
    const val auditDbName = "ota_audit.db"

    val deviceInfoServiceUuid: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
    val firmwareRevisionUuid: UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
    val softwareRevisionUuid: UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")
    val hardwareRevisionUuid: UUID = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb")
    val versionServiceUuid: UUID = UUID.fromString(BuildConfig.VERSION_SERVICE_UUID)
    val versionCharacteristicUuid: UUID = UUID.fromString(BuildConfig.VERSION_CHAR_UUID)
}

