package com.contentria.core.learning.kotlin

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderTest {

    @Test
    fun testOrder() {
        val order = Order(1L)

        assertThat(order.id).isEqualTo(1L)
    }
}