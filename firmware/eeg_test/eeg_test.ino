#include <bluefruit.h>
#include "version.h"

namespace {
constexpr char DEVICE_NAME[] = "XAIO-EEG-Test";
constexpr char MODEL_NAME[] = "XIAO-nRF52840-Test";
constexpr char MANUFACTURER_NAME[] = "XAIO";
constexpr char VERSION_SERVICE_UUID[] = "12345678-1234-1234-1234-123456789abc";
constexpr char VERSION_CHAR_UUID[] = "12345678-1234-1234-1234-123456789abd";
constexpr size_t VERSION_JSON_CAPACITY = 160;
}

BLEDfu bleDfu;
BLEDis bleDis;
BLEService versionService(VERSION_SERVICE_UUID);
BLECharacteristic versionChar(VERSION_CHAR_UUID);

char versionJson[VERSION_JSON_CAPACITY];

void updateVersionPayload() {
  snprintf(
      versionJson,
      sizeof(versionJson),
      "{\"fw\":\"%s\",\"code\":%d,\"epoch\":%d,\"channel\":\"%s\",\"hw\":\"%s\"}",
      FW_SEMVER,
      FW_VERSION_CODE,
      SECURITY_EPOCH,
      FW_CHANNEL,
      HW_REV);
}

void setupDeviceInformationService() {
  bleDis.setManufacturer(MANUFACTURER_NAME);
  bleDis.setModel(MODEL_NAME);
  bleDis.setFirmwareRev(FW_SEMVER);
  bleDis.setSoftwareRev(FW_CHANNEL);
  bleDis.setHardwareRev(HW_REV);
  bleDis.begin();
}

void setupVersionService() {
  updateVersionPayload();

  versionService.begin();
  versionChar.setProperties(CHR_PROPS_READ);
  versionChar.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  versionChar.setMaxLen(sizeof(versionJson) - 1);
  versionChar.begin();
  versionChar.write(versionJson, strlen(versionJson));
}

void startAdvertising() {
  Bluefruit.Advertising.stop();
  Bluefruit.ScanResponse.clearData();
  Bluefruit.Advertising.clearData();

  Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
  Bluefruit.Advertising.addTxPower();
  Bluefruit.Advertising.addName();
  Bluefruit.Advertising.addService(bleDis);
  Bluefruit.Advertising.addService(versionService);

  Bluefruit.ScanResponse.addName();

  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(32, 244);
  Bluefruit.Advertising.setFastTimeout(30);
  Bluefruit.Advertising.start(0);
}

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);

  Bluefruit.begin();
  Bluefruit.setName(DEVICE_NAME);
  Bluefruit.setTxPower(4);

  bleDfu.begin();
  setupDeviceInformationService();
  setupVersionService();
  startAdvertising();
}

void loop() {
  static uint32_t lastBlinkAt = 0;
  static bool ledOn = false;

  if (millis() - lastBlinkAt >= 1000) {
    lastBlinkAt = millis();
    ledOn = !ledOn;
    digitalWrite(LED_BUILTIN, ledOn ? LOW : HIGH);
  }
}

