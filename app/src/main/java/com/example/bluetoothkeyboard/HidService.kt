package com.example.bluetoothkeyboard

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

object HidService {
    val HID_SERVICE_UUID: UUID = UUID.fromString("00001812-0000-1000-8000-00805f9b34fb")
    private val HID_INFORMATION_UUID: UUID = UUID.fromString("00002a4a-0000-1000-8000-00805f9b34fb")
    private val REPORT_MAP_UUID: UUID = UUID.fromString("00002a4b-0000-1000-8000-00805f9b34fb")
    private val CONTROL_POINT_UUID: UUID = UUID.fromString("00002a4c-0000-1000-8000-00805f9b34fb")
    private val REPORT_UUID: UUID = UUID.fromString("00002a4d-0000-1000-8000-00805f9b34fb")
    private val PROTOCOL_MODE_UUID: UUID = UUID.fromString("00002a4e-0000-1000-8000-00805f9b34fb")

    private var gattServer: BluetoothGattServer? = null
    private var connectedDevice: BluetoothDevice? = null
    private lateinit var reportCharacteristic: BluetoothGattCharacteristic

    fun initialize(context: Context, connectionCallback: ((BluetoothDevice?, Boolean) -> Unit)? = null) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        gattServer = bluetoothManager.openGattServer(context, object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    connectedDevice = device
                    Log.d("HID", "Device connected: ${device.address}")
                    connectionCallback?.invoke(device, true)
                } else {
                    connectedDevice = null
                    Log.d("HID", "Device disconnected")
                    connectionCallback?.invoke(device, false)
                }
            }
        })

        // Characteristic: HID Information
        val hidInformationChar = BluetoothGattCharacteristic(
            HID_INFORMATION_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        hidInformationChar.value = byteArrayOf(
            0x01.toByte(), 0x01.toByte(), 0x00.toByte(), 0x03.toByte()
        ) // HID version 1.1, Country code 0, Flags 3

        // Characteristic: Report Map
        val reportMapChar = BluetoothGattCharacteristic(
            REPORT_MAP_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        reportMapChar.value = byteArrayOf(
            0x05.toByte(), 0x01.toByte(), 0x09.toByte(), 0x06.toByte(), 0xA1.toByte(), 0x01.toByte(), 0x05.toByte(), 0x07.toByte(),
            0x19.toByte(), 0xE0.toByte(), 0x29.toByte(), 0xE7.toByte(), 0x15.toByte(), 0x00.toByte(), 0x25.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x01.toByte(), 0x95.toByte(), 0x08.toByte(), 0x81.toByte(), 0x02.toByte(),
            0x95.toByte(), 0x01.toByte(), 0x75.toByte(), 0x08.toByte(), 0x81.toByte(), 0x01.toByte(),
            0x95.toByte(), 0x05.toByte(), 0x75.toByte(), 0x01.toByte(), 0x05.toByte(), 0x08.toByte(),
            0x19.toByte(), 0x01.toByte(), 0x29.toByte(), 0x05.toByte(), 0x91.toByte(), 0x02.toByte(),
            0x95.toByte(), 0x01.toByte(), 0x75.toByte(), 0x03.toByte(), 0x91.toByte(), 0x01.toByte(),
            0x95.toByte(), 0x06.toByte(), 0x75.toByte(), 0x08.toByte(), 0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x65.toByte(), 0x05.toByte(), 0x07.toByte(), 0x19.toByte(), 0x00.toByte(),
            0x29.toByte(), 0x65.toByte(), 0x81.toByte(), 0x00.toByte(), 0xC0.toByte()
        )


        // Characteristic: Control Point
        val controlPointChar = BluetoothGattCharacteristic(
            CONTROL_POINT_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        // Characteristic: Protocol Mode
        val protocolModeChar = BluetoothGattCharacteristic(
            PROTOCOL_MODE_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        protocolModeChar.value = byteArrayOf(0x01.toByte()) // Report Protocol mode

        // Characteristic: Report (Input)
        reportCharacteristic = BluetoothGattCharacteristic(
            REPORT_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or
                    BluetoothGattCharacteristic.PROPERTY_WRITE or
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        // Descriptor: Report Reference (for the input report)
        val reportRefDescriptor = BluetoothGattDescriptor(
            UUID.fromString("00002908-0000-1000-8000-00805f9b34fb"),
            BluetoothGattDescriptor.PERMISSION_READ
        )
        reportRefDescriptor.value = byteArrayOf(0x01.toByte(), 0x01.toByte()) // Report ID = 1, Input Report
        reportCharacteristic.addDescriptor(reportRefDescriptor)

        // Add all to HID Service
        val service = BluetoothGattService(HID_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        service.addCharacteristic(hidInformationChar)
        service.addCharacteristic(reportMapChar)
        service.addCharacteristic(controlPointChar)
        service.addCharacteristic(protocolModeChar)
        service.addCharacteristic(reportCharacteristic)

        gattServer?.addService(service)
    }

    fun sendReport(report: ByteArray) {
        connectedDevice?.let {
            reportCharacteristic.value = report
            gattServer?.notifyCharacteristicChanged(it, reportCharacteristic, false)
        }
    }
}
