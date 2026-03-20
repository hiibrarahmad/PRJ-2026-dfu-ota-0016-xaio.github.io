# Repo Split

This project is split into two repositories.

## App repo

- repository: `PRJ-2026-dfu-ota-0016-xaio.github.io`
- responsibility:
  - Android app
  - BLE scan and connect
  - version comparison
  - ZIP verification
  - Nordic DFU
  - audit log

## Firmware repo

- repository: `PRJ-2026-dfu-updtate-0017-xaio.github.io`
- responsibility:
  - firmware source
  - channel tagging
  - GitHub release assets
  - GitHub Pages metadata
  - release history
  - firmware documentation

## Why this split is better

- the Android app repo stays focused on mobile code
- the firmware repo keeps release history and OTA assets in one place
- documentation is clearer for both sides
- firmware publishing can evolve without cluttering the app repo

## Integration boundary

The only thing the app needs from the firmware repo is the published metadata and release assets.

That boundary is:

- `catalog.json`
- `releases.json`
- `firmware.zip`
- `firmware.zip.sig`
