package io.piestack.multiplatform.mpesa.common.api

data class MockResponse(
    val endpointSegment: String,
    val responseBody: String,
    val httpStatusCode: Int = 200
)