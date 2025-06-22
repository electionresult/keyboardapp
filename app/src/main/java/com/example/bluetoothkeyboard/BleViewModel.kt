package com.example.bluetoothkeyboard

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BleViewModel(app: Application) : AndroidViewModel(app) {
    private val context = app.applicationContext
    private val hidManager = HidPeripheralManager(context)
    private var initialized = false
    private var scanner: BluetoothLeScanner? = null
    private val discoveredDevices = mutableSetOf<BluetoothDevice>()
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            discoveredDevices.add(result.device)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { discoveredDevices.add(it.device) }
        }
    }

    private val _connectedDeviceName = MutableLiveData("Not connected")
    val connectedDeviceName: LiveData<String> = _connectedDeviceName

    private val _nearbyDevices = MutableLiveData<List<BluetoothDevice>>(emptyList())
    val nearbyDevices: LiveData<List<BluetoothDevice>> = _nearbyDevices

    fun initialize() {
        if (initialized) return
        initialized = true
        hidManager.initialize { device: BluetoothDevice?, connected: Boolean ->
            _connectedDeviceName.postValue(
                if (connected) "Connected: ${device?.name ?: "Unknown"}"
                else "Not connected"
            )
        }
        // Advertising is started inside HidPeripheralManager.initialize()
        // hidManager.startAdvertising()
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
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        scanner = bluetoothManager.adapter.bluetoothLeScanner
        discoveredDevices.clear()

        scanner?.startScan(scanCallback)

        viewModelScope.launch {
            delay(5000)
            scanner?.stopScan(scanCallback)
            _nearbyDevices.postValue(discoveredDevices.toList())
        }
    }

    override fun onCleared() {
        super.onCleared()
        scanner?.stopScan(scanCallback)
        if (initialized) {
            hidManager.stopAdvertising()
        }
    }
}
