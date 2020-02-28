package io.piestack.multiplatform.mpesa.common.helpers

import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.toByteArray
import io.piestack.multiplatform.mpesa.helpers.Base64Factory
import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun testEncodeToString() {
        checkEncodeToString("Kotlin is awesome", "S290bGluIGlzIGF3ZXNvbWU=")
    }

    @Test
    fun testPaddedStrings() {
        checkEncodeToString("", "")
        checkEncodeToString("1", "MQ==")
        checkEncodeToString("22", "MjI=")
        checkEncodeToString("333", "MzMz")
        checkEncodeToString("4444", "NDQ0NA==")

    }

    private fun checkEncodeToString(input: String, expectedOutput: String) {
        assertEquals(
            expectedOutput,
            Base64Factory.createEncoder().encodeToString(input.toByteArray(Charset.forName("ISO-8859-1")))
        )
    }
}