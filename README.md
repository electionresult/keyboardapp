# Bluetooth Keyboard App

This project is an Android application that emulates a Bluetooth Low Energy (BLE) keyboard. It allows text to be sent from your Android device to a paired host device using the BLE HID profile.

## Building

Use Gradle to build the debug APK:

```bash
./gradlew assembleDebug
```

The generated APK can be found in `app/build/outputs/apk/debug/`.

## Usage

1. Install the generated APK on an Android device that supports BLE peripheral mode.
2. Launch the app and grant the requested Bluetooth permissions.
3. If prompted, enable Bluetooth so the device can advertise and pair.
4. Enter text and press **Send** to transmit it as keyboard input to the connected device.
5. Use **Scan Nearby Devices** to discover BLE devices around you.

This repository also contains `blehid-lib`, a library used by the app to handle BLE HID operations.
