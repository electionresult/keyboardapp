package com.example.bluetoothkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bluetoothkeyboard.ui.theme.BluetoothKeyboardTheme

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.widget.Toast

class MainActivity : ComponentActivity() {

    private val viewModel: BleViewModel by viewModels()
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkBleSupport()) {
            Toast.makeText(this, "BLE Peripheral not supported", Toast.LENGTH_LONG).show()
            finish()
        }

        requestPermissions()

        setContent {
            BluetoothKeyboardTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UiScreen(viewModel)
                }
            }
        }
    }

    private fun checkBleSupport(): Boolean {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter != null && packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) &&
                bluetoothAdapter.isMultipleAdvertisementSupported
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        permissionLauncher.launch(permissions)
    }

}