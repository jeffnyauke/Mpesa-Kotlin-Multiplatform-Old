package io.piestack.multiplatform.mpesa.common.coroutines

internal actual fun <T> runTest(block: suspend () -> T): dynamic {
    return promise { block() }
}