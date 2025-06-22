package com.example.bluetoothkeyboard

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import jp.kshoji.blehid.KeyboardPeripheral

class HidPeripheralManager(private val context: Context) {
    private var keyboardPeripheral: KeyboardPeripheral? = null
    private val handler = Handler(Looper.getMainLooper())

    fun initialize(onConnectionChanged: (BluetoothDevice?, Boolean) -> Unit) {
        keyboardPeripheral = KeyboardPeripheral(context)

        // Set connection callbacks on instance, not class
        keyboardPeripheral?.setKeyboardConnectionCallback(object :
            KeyboardPeripheral.KeyboardConnectionCallback {
            override fun onKeyboardConnected(device: BluetoothDevice) {
                onConnectionChanged(device, true)
            }

            override fun onKeyboardDisconnected(device: BluetoothDevice) {
                onConnectionChanged(device, false)
                // restart advertising after a short delay to allow reconnection
                handler.postDelayed({ startAdvertising() }, 3000)
            }
        })

        startAdvertising()
    }

    fun startAdvertising() {
        try {
            keyboardPeripheral?.startAdvertising()
        } catch (e: Exception) {
            Log.e("HID", "Failed to start advertising", e)
        }
    }

    fun stopAdvertising() {
        try {
            keyboardPeripheral?.stopAdvertising()
        } catch (e: Exception) {
            Log.e("HID", "Failed to stop advertising", e)
        }
    }

    fun sendKey(char: Char) {
        val modifier = KeyboardPeripheral.modifier(char.toString())
        val keyCode = KeyboardPeripheral.keyCode(char.toString())
        if (keyCode.toInt() != 0) {
            keyboardPeripheral?.sendKeyDown(modifier, keyCode)
            keyboardPeripheral?.sendKeyUp()
        }
    }
}
