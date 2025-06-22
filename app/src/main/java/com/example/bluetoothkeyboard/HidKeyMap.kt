package com.example.bluetoothkeyboard

object HidKeyMap {
    private val map = mapOf(
        'a' to 0x04, 'b' to 0x05, 'c' to 0x06, 'd' to 0x07,
        'e' to 0x08, 'f' to 0x09, 'g' to 0x0A, 'h' to 0x0B,
        'i' to 0x0C, 'j' to 0x0D, 'k' to 0x0E, 'l' to 0x0F,
        'm' to 0x10, 'n' to 0x11, 'o' to 0x12, 'p' to 0x13,
        'q' to 0x14, 'r' to 0x15, 's' to 0x16, 't' to 0x17,
        'u' to 0x18, 'v' to 0x19, 'w' to 0x1A, 'x' to 0x1B,
        'y' to 0x1C, 'z' to 0x1D, ' ' to 0x2C
    )

    fun getUsageId(c: Char): Int {
        return map[c.lowercaseChar()] ?: 0
    }
}
