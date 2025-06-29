package com.example.testci_cd.ui

import org.junit.Test

class SumTest {

    @Test
    fun testSumTwoNumbers() {
        val sum = Sum()
        val result = sum.sumTwoNumbers(2, 3)
        assert(result == 5) { "Expected 5, but got $result" }

    }
}