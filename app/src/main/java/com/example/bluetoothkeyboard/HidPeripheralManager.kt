package com.example.bluetoothkeyboard

import android.bluetooth.BluetoothDevice
import android.content.Context
import jp.kshoji.blehid.KeyboardPeripheral

class HidPeripheralManager(private val context: Context) {
    private var keyboardPeripheral: KeyboardPeripheral? = null

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
            }
        })

        keyboardPeripheral?.startAdvertising()
    }

    fun startAdvertising() {
        keyboardPeripheral?.startAdvertising()
    }

    fun stopAdvertising() {
        keyboardPeripheral?.stopAdvertising()
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
