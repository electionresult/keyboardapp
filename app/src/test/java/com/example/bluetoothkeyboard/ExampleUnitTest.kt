package com.example.bluetoothkeyboard

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun hidKeyMap_returnsCorrectCodes() {
        assertEquals(0x04, HidKeyMap.getUsageId('a'))
        assertEquals(0x05, HidKeyMap.getUsageId('B'))
        assertEquals(0x2C, HidKeyMap.getUsageId(' '))
    }
}
