# App Setup

This document explains how the Android app is wired to the dedicated firmware release repository.

## 1. Firmware repo location

The app reads OTA metadata from:

- `githubOwner` in `gradle.properties`
- `releaseRepo` in `gradle.properties`

Current values:

- `githubOwner=hiibrarahmad`
- `releaseRepo=PRJ-2026-dfu-updtate-0017-xaio.github.io`

## 2. What URLs the app builds

The app generates:

- `catalog.json`
- `releases.json`

From those properties:

- `https://<owner>.github.io/<repo>/catalog.json`
- `https://<owner>.github.io/<repo>/releases.json`

## 3. What the app expects from the firmware repo

Each release record must include:

- version
- version code
- channel
- hardware allow list
- SHA-256
- signature URL
- release notes
- `dfu_package_format`

Current supported package format:

- `legacy-crc`

## 4. If the firmware repo name changes

Update `gradle.properties`, then rebuild the app:

```powershell
.\gradlew.bat :app:assembleDebug
```

## 5. Public key requirement

The app verifies the firmware ZIP using:

- `app/src/main/assets/ota_app_signature_public.pem`

That public key must match the private key used in the firmware repo release workflow.

## 6. Runtime flow

At runtime the app:

1. scans BLE devices
2. reads the device version
3. fetches latest firmware metadata
4. filters out unsupported package formats
5. verifies ZIP checksum and signature
6. starts DFU
