package com.example.bluetoothkeyboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun UiScreen(viewModel: BleViewModel) {
    var inputText by remember { mutableStateOf("") }
    var speed by remember { mutableStateOf(50f) }
    val deviceStatus by viewModel.connectedDeviceName.observeAsState("Not connected")
    val nearbyDevices by viewModel.nearbyDevices.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Typing speed: ${speed.toInt()} ms/char")
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0f..500f
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.sendTextAsKeyboard(inputText, speed.toLong())
            }) {
                Text("Send")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                viewModel.discoverNearbyDevices()
            }) {
                Text("Scan Nearby Devices")
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(nearbyDevices) { device ->
                    Text("• ${device.name ?: "Unnamed"} - ${device.address}")
                }
            }
        }

        // Bottom display of connected device
        Text(
            text = deviceStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
    }
}

