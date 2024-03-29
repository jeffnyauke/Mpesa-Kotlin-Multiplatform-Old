package io.piestack.multiplatform.mpesa

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.toByteArray
import io.piestack.multiplatform.mpesa.error.*
import io.piestack.multiplatform.mpesa.helpers.Base64Factory
import io.piestack.multiplatform.mpesa.helpers.generatePassword
import io.piestack.multiplatform.mpesa.helpers.generateTimestamp
import io.piestack.multiplatform.mpesa.model.enums.Environment
import io.piestack.multiplatform.mpesa.model.enums.TransactionType
import io.piestack.multiplatform.mpesa.model.requests.LipaNaMpesaOnlinePaymentRequest
import io.piestack.multiplatform.mpesa.model.requests.LipaNaMpesaOnlineQueryRequest
import io.piestack.multiplatform.mpesa.model.responses.AuthResponse
import io.piestack.multiplatform.mpesa.model.responses.LipaNaMpesaOnlinePaymentResponse
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class Mpesa(
    httpClientEngine: HttpClientEngine? = null,
    private val appKey: String,
    private val appSecret: String,
    private val environment: Environment,
    private val shortCode: String? = null,
    private val initiatorName: String? = null,
    private val lipaNaMpesaShortCode: String? = null,
    private val lipaNaMpesaShortPass: String? = null,
    private val securityCredential: String? = null
) {

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

        val authResponse = Json.parse(AuthResponse.serializer(), authResponseJson)

        Either.Right(authResponse)
    } catch (e: Exception) {
        handleError(e)
    }

    @UnstableDefault
    suspend fun lipaNaMpesaOnlinePayment(
        amount: String,
        phoneNumber: String,
        callBackURL: String,
        queueTimeOutURL: String,
        accountReference: String,
        transactionDesc: String = "Lipa na mpesa online payment"
    ): LipaNaMpesaOnlinePaymentResponse {
        when {
            shortCode == null -> throw IllegalArgumentException("Shortcode should exist. Provide the value in your Mpesa instance.")
            lipaNaMpesaShortPass == null -> throw IllegalArgumentException("Lipa na Mpesa Passkey should exist. Provide the value in your Mpesa instance.")
            else -> {
                val timestamp = generateTimestamp()
                val password = generatePassword(shortCode, lipaNaMpesaShortPass, timestamp)
                val requestObject = LipaNaMpesaOnlinePaymentRequest(
                    shortCode,
                    password,
                    timestamp,
                    TransactionType.CustomerPayBillOnline.name,
                    amount,
                    phoneNumber,
                    phoneNumber,
                    shortCode,
                    callBackURL,
                    accountReference,
                    queueTimeOutURL,
                    transactionDesc
                )

                when (val authResponse = authenticate()) {
                    is Either.Left -> throw Error("Invalid credentials: ${authResponse.value}")
                    is Either.Right -> {
                        return client.post {
                            url("${environment.baseUrl}/mpesa/stkpush/v1/processrequest")
                            body = requestObject
                            header("content-type", "application/json")
                            header("authorization", "Bearer ${authResponse.value.accessToken}")
                        }
                    }
                }
            }
        }
    }

    @UnstableDefault
    suspend fun lipaNaMpesaOnlineQueryRequest(
        checkoutRequestID: String
    ): LipaNaMpesaOnlinePaymentResponse {
        when {
            shortCode == null -> throw IllegalArgumentException("Shortcode should exist. Provide the value in your Mpesa instance.")
            lipaNaMpesaShortPass == null -> throw IllegalArgumentException("Lipa na Mpesa Passkey should exist. Provide the value in your Mpesa instance.")
            else -> {
                val timestamp = generateTimestamp()
                val password = generatePassword(shortCode, lipaNaMpesaShortPass, timestamp)
                val requestObject = LipaNaMpesaOnlineQueryRequest(
                    shortCode,
                    password,
                    timestamp,
                    TransactionType.CustomerPayBillOnline.name
                )

                when (val authResponse = authenticate()) {
                    is Either.Left -> throw IllegalArgumentException("Invalid credentials: ${authResponse.value}")
                    is Either.Right -> {
                        return client.post {
                            url("${environment.baseUrl}/mpesa/stkpush/v1/processrequest")
                            body = requestObject
                            header("content-type", "application/json")
                            header("authorization", "Bearer ${authResponse.value.accessToken}")
                        }
                    }
                }
            }
        }
    }

    private val client: HttpClient = HttpClient(httpClientEngine!!) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()

        }
    }

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