package com.example.bluetoothkeyboard

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BleViewModel(app: Application) : AndroidViewModel(app) {
    private val context = app.applicationContext
    private val hidManager = HidPeripheralManager(context)

    private val _connectedDeviceName = MutableLiveData("Not connected")
    val connectedDeviceName: LiveData<String> = _connectedDeviceName

    private val _nearbyDevices = MutableLiveData<List<BluetoothDevice>>(emptyList())
    val nearbyDevices: LiveData<List<BluetoothDevice>> = _nearbyDevices

    init {
        hidManager.initialize { device: BluetoothDevice?, connected: Boolean ->
            _connectedDeviceName.postValue(
                if (connected) "Connected: ${device?.name ?: "Unknown"}"
                else "Not connected"
            )
        }
        hidManager.startAdvertising()
    }

    fun sendTextAsKeyboard(text: String, delayMs: Long) {
        viewModelScope.launch {
            text.forEach { char ->
                hidManager.sendKey(char)
                delay(delayMs)
            }
        }
    }

    fun discoverNearbyDevices() {
        // 🚧 Add real BLE scan logic here later
        _nearbyDevices.postValue(emptyList()) // Right now returns nothing
    }

    override fun onCleared() {
        super.onCleared()
        hidManager.stopAdvertising()
    }
}
