package io.piestack.multiplatform.mpesa.common.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

internal actual fun <T> runTest(block: suspend () -> T): dynamic {
    return GlobalScope.promise { block() }
}