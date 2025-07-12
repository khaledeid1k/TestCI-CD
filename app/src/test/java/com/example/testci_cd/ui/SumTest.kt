package com.example.testci_cd.ui

import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.test.assertEquals

class SumTest {

    @Test
    fun testSumTwoNumbers() {
        val sum = Sum()
        val result = sum.sumTwoNumbers(2, 3)
        assertEquals(6, result, "Expected 5, but got $result")
    }
}