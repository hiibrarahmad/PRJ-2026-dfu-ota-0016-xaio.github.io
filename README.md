# XAIO OTA Android App

This repository contains the Android application for the XIAO nRF52840 OTA update flow.

The firmware release source lives in a separate repository:

- Firmware repo: `https://github.com/hiibrarahmad/PRJ-2026-dfu-updtate-0017-xaio.github.io`

The app reads OTA metadata from that firmware repo’s GitHub Pages site:

- `https://hiibrarahmad.github.io/PRJ-2026-dfu-updtate-0017-xaio.github.io/catalog.json`
- `https://hiibrarahmad.github.io/PRJ-2026-dfu-updtate-0017-xaio.github.io/releases.json`

## What lives here

- Android app source in `app`
- BLE scan and version-read flow
- OTA policy checks
- Nordic DFU integration
- audit log export
- app-side ZIP signature verification

## Repo split

This repo is app-only.

Use the firmware repo for:

- firmware code changes
- channel releases
- GitHub Actions firmware builds
- GitHub Pages release metadata
- release notes and OTA publishing

## Current firmware source

The app is currently pointed at:

- owner: `hiibrarahmad`
- release repo: `PRJ-2026-dfu-updtate-0017-xaio.github.io`

That mapping is stored in `gradle.properties`.

## Build the app

```powershell
.\gradlew.bat :app:assembleDebug
```

The APK is produced at:

- `app/build/outputs/apk/debug/app-debug.apk`

Install to a phone:

```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## How the app decides what to show

1. Scan for nearby BLE devices
2. Connect and read installed version data
3. Fetch the latest metadata from the firmware repo
4. Show:
   - no update
   - upgrade
   - reinstall
   - downgrade
5. Verify SHA-256 and ZIP signature
6. Start Nordic DFU with XIAO-safe settings

## Important note about compatibility

The app only accepts firmware releases marked with:

- `dfu_package_format = legacy-crc`

This avoids offering old incompatible packages that the current XIAO bootloader would reject.

## Docs

- [App Setup](docs/app-setup.md)
- [Repo Split](docs/repo-split.md)
