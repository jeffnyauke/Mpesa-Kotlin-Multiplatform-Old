package io.piestack.multiplatform.mpesa

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.toByteArray
import io.piestack.multiplatform.mpesa.error.*
import io.piestack.multiplatform.mpesa.helpers.Base64Factory
import io.piestack.multiplatform.mpesa.model.AuthResponse
import io.piestack.multiplatform.mpesa.model.Task
import io.piestack.multiplatform.mpesa.model.enums.Environment
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class Mpesa(
    httpClientEngine: HttpClientEngine? = null,
    private val appKey: String,
    private val appSecret: String,
    private val environment: Environment
) {

    private val client: HttpClient = HttpClient(httpClientEngine!!) {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                // It's necessary register the serializer because:
                // Obtaining serializer from KClass is not available on native
                // due to the lack of reflection
                register(Task.serializer())
            }
        }
    }

    @UnstableDefault
    private suspend fun authenticate(): Either<ApiError, AuthResponse> = try {
        val appKeySecret = "$appKey:$appSecret"
        val bytes = appKeySecret.toByteArray(Charset.forName("ISO-8859-1"))
        val encoded = Base64Factory.createEncoder().encodeToString(bytes)

        val authResponseJson = client.get<String> {
            url("${environment.baseUrl}/oauth/v1/generate?grant_type=client_credentials")
            header("authorization", "Basic $encoded")
            header("cache-control", "no-cache")
        }

        // JsonFeature does not working currently with root-level array
        // https://github.com/Kotlin/kotlinx.serialization/issues/179
        val authResponse = Json.nonstrict.parse(AuthResponse.serializer(), authResponseJson)

        Either.Right(authResponse)
    } catch (e: Exception) {
        handleError(e)
    }

    /*@UnstableDefault
    private suspend fun stkPush(): Either<ApiError, AuthResponse> = try {
        val appKeySecret = "$appKey:$appSecret"
        val bytes = appKeySecret.toByteArray(Charset.forName("ISO-8859-1"))
        val encoded = Base64Factory.createEncoder().encodeToString(bytes)

        val authResponseJson = client.get<String> {
            url("${environment.baseUrl}/oauth/v1/generate?grant_type=client_credentials")
            header("authorization", "Basic $encoded")
            header("cache-control", "no-cache")
        }

        // JsonFeature does not working currently with root-level array
        // https://github.com/Kotlin/kotlinx.serialization/issues/179
        val authResponse = Json.nonstrict.parse(AuthResponse.serializer(), authResponseJson)

        Either.Right(authResponse)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun stkPush(
        businessShortCode: String,
        passKey: String,
        transactionType: String,
        amount: String,
        phoneNumber: String,
        partyA: String,
        partyB: String,
        callBackURL: String,
        queueTimeOutURL: String,
        accountReference: String,
        transactionDesc: String
    ): Response.StkResponse {
        val timestamp = generateTimestamp()
        val password = generatePassword(businessShortCode, passKey, timestamp)
        val requestObject = Stk(
            businessShortCode,
            password,
            timestamp,
            transactionType,
            amount,
            phoneNumber,
            partyA,
            partyB,
            callBackURL,
            accountReference,
            queueTimeOutURL,
            transactionDesc
        )

        val authResponse = authenticate()

        return client.post {
            url("${env.baseUrl}/mpesa/stkpush/v1/processrequest")
            body = requestObject
            header("content-type", "application/json")
            header("authorization", "Bearer ${authResponse.accessToken}")
        }

    }*/

    /*tasksResponse.fold(
    { left -> fail("Should return right but was left: $left") },
    { right ->
        assertEquals(4, right.size.toLong())
        assertTaskContainsExpectedValues(right[0])
    })*/

    private fun handleError(exception: Exception): Either<ApiError, Nothing> =
        if (exception is ResponseException) {
            when (exception.response.status.value) {
                404 -> {
                    Either.Left(ItemNotFoundError)
                }
                400 -> {
                    Either.Left(AuthenticationError(exception))
                }
                else -> {
                    Either.Left(UnknownError(exception.response.status.value))
                }
            }
        } else {
            Either.Left(NetworkError)
        }
}