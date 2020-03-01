package io.piestack.multiplatform.mpesa.helpers

import com.soywiz.klock.DateTime
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.toByteArray

fun generateTimestamp(): String {
    return DateTime.now().time.toString()
}

fun generatePassword(businessShortCode: String, passKey: String, timeStamp: String): String {
    val password = businessShortCode + passKey + timeStamp
    val bytes = password.toByteArray(Charset.forName("ISO-8859-1"))
    return Base64Factory.createEncoder().encodeToString(bytes)
}