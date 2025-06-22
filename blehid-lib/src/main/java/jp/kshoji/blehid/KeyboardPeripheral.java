package jp.kshoji.blehid;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;

public final class KeyboardPeripheral extends HidPeripheral {
    private static final String TAG = KeyboardPeripheral.class.getSimpleName();

    public interface KeyboardConnectionCallback {
        void onKeyboardConnected(BluetoothDevice device);
        void onKeyboardDisconnected(BluetoothDevice device);
    }

    private KeyboardConnectionCallback keyboardConnectionCallback;

    public void setKeyboardConnectionCallback(KeyboardConnectionCallback callback) {
        this.keyboardConnectionCallback = callback;
    }

    public void handleDeviceConnected(BluetoothDevice device) {
        if (keyboardConnectionCallback != null) {
            keyboardConnectionCallback.onKeyboardConnected(device);
        }
    }

    public void handleDeviceDisconnected(BluetoothDevice device) {
        if (keyboardConnectionCallback != null) {
            keyboardConnectionCallback.onKeyboardDisconnected(device);
        }
    }

    public KeyboardPeripheral(final Context context) throws UnsupportedOperationException {
        super(context.getApplicationContext(), true, true, false, 20);
    }

    private static final int KEY_PACKET_MODIFIER_KEY_INDEX = 0;
    private static final int KEY_PACKET_KEY_INDEX = 2;
    private static final byte[] EMPTY_REPORT = new byte[8];

    public void sendKeys(final String text) {
        String lastKey = null;
        for (int i = 0; i < text.length(); i++) {
            final String key = text.substring(i, i + 1);
            final byte[] report = new byte[8];
            report[KEY_PACKET_MODIFIER_KEY_INDEX] = modifier(key);
            report[KEY_PACKET_KEY_INDEX] = keyCode(key);

            if (key.equals(lastKey)) {
                sendKeyUp();
            }
            addInputReport(report);
            lastKey = key;
        }
        sendKeyUp();
    }

    public void sendKeyDown(final byte modifier, final byte keyCode) {
        final byte[] report = new byte[8];
        report[KEY_PACKET_MODIFIER_KEY_INDEX] = modifier;
        report[KEY_PACKET_KEY_INDEX] = keyCode;
        addInputReport(report);
    }

    public void sendKeyUp() {
        addInputReport(EMPTY_REPORT);
    }

    @Override
    protected void onOutputReport(final byte[] outputReport) {
        Log.i(TAG, "onOutputReport data: " + Arrays.toString(outputReport));
    }

    @Override
    protected byte[] getReportMap() {
        return REPORT_MAP;
    }

    private static final byte[] REPORT_MAP = {
            // HID Report Descriptor for standard keyboard
            (byte) 0x05, (byte) 0x01,       // Usage Page (Generic Desktop)
            (byte) 0x09, (byte) 0x06,       // Usage (Keyboard)
            (byte) 0xA1, (byte) 0x01,       // Collection (Application)
            (byte) 0x05, (byte) 0x07,       //   Usage Page (Key Codes)
            (byte) 0x19, (byte) 0xE0,       //   Usage Minimum (224)
            (byte) 0x29, (byte) 0xE7,       //   Usage Maximum (231)
            (byte) 0x15, (byte) 0x00,       //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x01,       //   Logical Maximum (1)
            (byte) 0x75, (byte) 0x01,       //   Report Size (1)
            (byte) 0x95, (byte) 0x08,       //   Report Count (8)
            (byte) 0x81, (byte) 0x02,       //   Input (Data, Variable, Absolute) ; Modifier byte
            (byte) 0x95, (byte) 0x01,       //   Report Count (1)
            (byte) 0x75, (byte) 0x08,       //   Report Size (8)
            (byte) 0x81, (byte) 0x01,       //   Input (Constant) ; Reserved byte
            (byte) 0x95, (byte) 0x05,       //   Report Count (5)
            (byte) 0x75, (byte) 0x01,       //   Report Size (1)
            (byte) 0x05, (byte) 0x08,       //   Usage Page (LEDs)
            (byte) 0x19, (byte) 0x01,       //   Usage Minimum (1)
            (byte) 0x29, (byte) 0x05,       //   Usage Maximum (5)
            (byte) 0x91, (byte) 0x02,       //   Output (Data, Variable, Absolute) ; LED report
            (byte) 0x95, (byte) 0x01,       //   Report Count (1)
            (byte) 0x75, (byte) 0x03,       //   Report Size (3)
            (byte) 0x91, (byte) 0x01,       //   Output (Constant) ; Padding
            (byte) 0x95, (byte) 0x06,       //   Report Count (6)
            (byte) 0x75, (byte) 0x08,       //   Report Size (8)
            (byte) 0x15, (byte) 0x00,       //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x65,       //   Logical Maximum (101)
            (byte) 0x05, (byte) 0x07,       //   Usage Page (Key codes)
            (byte) 0x19, (byte) 0x00,       //   Usage Minimum (0)
            (byte) 0x29, (byte) 0x65,       //   Usage Maximum (101)
            (byte) 0x81, (byte) 0x00,       //   Input (Data, Array)
            (byte) 0xC0                  // End Collection
    };

    public static byte modifier(final String aChar) {
        switch (aChar) {
            case "A": case "B": case "C": case "D": case "E": case "F":
            case "G": case "H": case "I": case "J": case "K": case "L":
            case "M": case "N": case "O": case "P": case "Q": case "R":
            case "S": case "T": case "U": case "V": case "W": case "X":
            case "Y": case "Z": case "!": case "@": case "#": case "$":
            case "%": case "^": case "&": case "*": case "(": case ")":
            case "_": case "+": case "{": case "}": case "|": case ":":
            case "\"": case "~": case "<": case ">": case "?":
                return 0x02; // SHIFT
            default:
                return 0x00;
        }
    }

    public static byte keyCode(final String aChar) {
        switch (aChar) {
            case "a": case "A": return 0x04;
            case "b": case "B": return 0x05;
            case "c": case "C": return 0x06;
            case "d": case "D": return 0x07;
            case "e": case "E": return 0x08;
            case "f": case "F": return 0x09;
            case "g": case "G": return 0x0A;
            case "h": case "H": return 0x0B;
            case "i": case "I": return 0x0C;
            case "j": case "J": return 0x0D;
            case "k": case "K": return 0x0E;
            case "l": case "L": return 0x0F;
            case "m": case "M": return 0x10;
            case "n": case "N": return 0x11;
            case "o": case "O": return 0x12;
            case "p": case "P": return 0x13;
            case "q": case "Q": return 0x14;
            case "r": case "R": return 0x15;
            case "s": case "S": return 0x16;
            case "t": case "T": return 0x17;
            case "u": case "U": return 0x18;
            case "v": case "V": return 0x19;
            case "w": case "W": return 0x1A;
            case "x": case "X": return 0x1B;
            case "y": case "Y": return 0x1C;
            case "z": case "Z": return 0x1D;
            case "1": case "!": return 0x1E;
            case "2": case "@": return 0x1F;
            case "3": case "#": return 0x20;
            case "4": case "$": return 0x21;
            case "5": case "%": return 0x22;
            case "6": case "^": return 0x23;
            case "7": case "&": return 0x24;
            case "8": case "*": return 0x25;
            case "9": case "(": return 0x26;
            case "0": case ")": return 0x27;
            case "\n": return 0x28;
            case "\b": return 0x2A;
            case "\t": return 0x2B;
            case " ": return 0x2C;
            case "-": case "_": return 0x2D;
            case "=": case "+": return 0x2E;
            case "[": case "{": return 0x2F;
            case "]": case "}": return 0x30;
            case "\\": case "|": return 0x31;
            case ";": case ":": return 0x33;
            case "'": case "\"": return 0x34;
            case "`": case "~": return 0x35;
            case ",": case "<": return 0x36;
            case ".": case ">": return 0x37;
            case "/": case "?": return 0x38;
            default: return 0;
        }
    }
}
