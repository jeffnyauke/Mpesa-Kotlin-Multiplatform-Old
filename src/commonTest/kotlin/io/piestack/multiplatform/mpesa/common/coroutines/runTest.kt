package io.piestack.multiplatform.mpesa.common.coroutines

internal expect fun <T> runTest(block: suspend () -> T): T
