# OTA Architecture

## Firmware

- Arduino sketch runs on `Seeeduino:nrf52:xiaonRF52840`.
- BLE Device Information Service exposes firmware version, channel, and hardware revision.
- `BLEDfu` enables buttonless entry into the Nordic Secure DFU bootloader.
- A custom characteristic exposes structured version data including `version_code` and `security_epoch`.

## Release Metadata

GitHub Pages serves two files:

- `catalog.json`: latest release per channel
- `releases.json`: full release history per channel for downgrade selection and release-note display

## Android App

- Reads the device version over BLE.
- Fetches GitHub Pages metadata with a one-hour cache.
- Compares installed firmware with the latest channel release.
- Shows no-update, upgrade, reinstall, or downgrade paths.
- Records an audit entry for every install attempt and completed user action.
- Downloads the DFU ZIP and signature, verifies SHA-256 and the app-side signature, then starts Nordic DFU.

## GitHub Workflows

- `build-firmware.yml`: tag-triggered compile and draft release
- `publish-pages.yml`: release-published trigger that updates and deploys Pages metadata

