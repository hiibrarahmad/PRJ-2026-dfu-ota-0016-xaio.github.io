# Test Firmware Matrix

These OTA demo builds all use the same sketch at [firmware/eeg_test/eeg_test.ino](/d:/PRJ-2026-dfu-ota-0016-xaio.github.io/firmware/eeg_test/eeg_test.ino), but the LED behavior changes based on the firmware version and release channel.

## Visible behavior

| Channel / Version | LED behavior |
| --- | --- |
| `dev` `0.1.0` | Solid red |
| `dev` `0.1.5` and newer dev builds | Solid green |
| `beta` | Blue blink |
| `stable` | RGB cycle: red -> green -> blue |

## Suggested release tags

- `v0.1.0-dev`: red baseline firmware already on the board
- `v0.1.6-dev`: green dev update for quick OTA verification
- `v0.2.0-beta`: blue beta example
- `v1.0.0-stable`: RGB stable example

## Notes

- The XIAO nRF52840 RGB LEDs are active-low in the Seeed nRF52 core, so `LOW` turns a color on.
- If you want the currently published `v0.1.5-dev` release to show the new green behavior, it must be rebuilt from the updated sketch or replaced with a new dev tag.
