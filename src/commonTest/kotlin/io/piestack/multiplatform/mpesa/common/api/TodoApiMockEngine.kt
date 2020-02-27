package io.piestack.multiplatform.mpesa.common.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.content.TextContent
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlin.test.assertEquals

class TodoApiMockEngine {
    private lateinit var mockResponse: MockResponse
    private var lastRequest: HttpRequestData? = null

    fun enqueueMockResponse(
        endpointSegment: String,
        responseBody: String,
        httpStatusCode: Int = 200
    ) {
        mockResponse = MockResponse(endpointSegment, responseBody, httpStatusCode)
    }

    fun get() = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                lastRequest = request
                when (request.url.encodedPath) {
                    mockResponse.endpointSegment -> {
                        val responseHeaders =
                            headersOf(HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString()))
                        respond(
                            content = ByteReadChannel(mockResponse.responseBody.toByteArray(Charsets.UTF_8)),
                            status = HttpStatusCode.fromValue(mockResponse.httpStatusCode),
                            headers = responseHeaders
                        )
                    }
                    else -> error("Unhandled ${request.url.fullPath}")
                }
            }
        }
    }

    fun verifyRequestContainsHeader(key: String, expectedValue: String) {
        val value = lastRequest!!.headers[key]
        assertEquals(expectedValue, value)
    }

    fun verifyRequestBody(addTaskRequest: String) {
        val body = (lastRequest!!.body as TextContent).text

        assertEquals(addTaskRequest, body)
    }

    fun verifyGetRequest() {
        assertEquals(HttpMethod.Get.value, lastRequest!!.method.value)
    }

    fun verifyPostRequest() {
        assertEquals(HttpMethod.Post.value, lastRequest!!.method.value)
    }

    fun verifyPutRequest() {
        assertEquals(HttpMethod.Put.value, lastRequest!!.method.value)
    }

    fun verifyDeleteRequest() {
        assertEquals(HttpMethod.Delete.value, lastRequest!!.method.value)
    }
}