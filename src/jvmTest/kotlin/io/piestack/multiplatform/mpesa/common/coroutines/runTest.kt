package io.piestack.multiplatform.mpesa.common.coroutines

import kotlinx.coroutines.runBlocking

internal actual fun <T> runTest(block: suspend () -> T): T {
    return runBlocking { block() }
}