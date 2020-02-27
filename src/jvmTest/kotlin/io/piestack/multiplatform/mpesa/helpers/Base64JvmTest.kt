package io.piestack.multiplatform.mpesa.helpers

import org.junit.Test
import kotlin.test.assertEquals

class Base64JvmTest {

    @Test
    fun testNonAsciiString() {
        val utf8String = "Gödel"
        val actual = Base64Factory.createEncoder().encodeToString(utf8String.toByteArray())
        assertEquals("R8O2ZGVs", actual)
    }
}