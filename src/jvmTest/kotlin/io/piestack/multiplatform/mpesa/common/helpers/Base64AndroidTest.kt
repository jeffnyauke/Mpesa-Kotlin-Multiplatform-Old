package io.piestack.multiplatform.mpesa.common.helpers

import io.piestack.multiplatform.mpesa.helpers.Base64Factory
import org.junit.Test
import kotlin.test.assertEquals

class Base64AndroidTest {

    @Test
    fun testNonAsciiString() {
        val utf8String = "Gödel"
        val actual = Base64Factory.createEncoder().encodeToString(utf8String.toByteArray())
        assertEquals("R8O2ZGVs", actual)
    }
}