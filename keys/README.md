# Key Setup

This project uses two signing layers:

## 1. Secure DFU Bootloader Signing Key

Purpose:

- Validates the DFU init packet in the nRF52 bootloader.
- Required for professional Secure DFU.

Generate locally:

```bash
adafruit-nrfutil keys --gen-key dfu_bootloader_private.pem
adafruit-nrfutil keys --show-vk pem dfu_bootloader_private.pem > dfu_bootloader_public.pem
```

Store on GitHub:

- Secret: `DFU_BOOTLOADER_PRIVATE_KEY_PEM_BASE64`

Notes:

- Base64-encode the private key before storing it as a secret.
- The matching public key must be compiled into the bootloader image on the device.

## 2. Android App ZIP Signature Key

Purpose:

- Lets the Android app verify that the downloaded ZIP came from your release pipeline before it starts DFU.

Generate locally:

```bash
openssl genrsa -out app_signature_private.pem 2048
openssl rsa -in app_signature_private.pem -pubout -out app_signature_public.pem
```

Store on GitHub:

- Secret: `APP_SIGNATURE_PRIVATE_KEY_PEM_BASE64`

Distribute publicly:

- Copy the public key into `app/src/main/assets/ota_app_signature_public.pem`

Do not commit private keys.

