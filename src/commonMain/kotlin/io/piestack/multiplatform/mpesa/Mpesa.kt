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
import io.piestack.multiplatform.mpesa.error.ApiError
import io.piestack.multiplatform.mpesa.error.ItemNotFoundError
import io.piestack.multiplatform.mpesa.error.NetworkError
import io.piestack.multiplatform.mpesa.error.UnknownError
import io.piestack.multiplatform.mpesa.helpers.Base64Factory
import io.piestack.multiplatform.mpesa.model.AuthResponse
import io.piestack.multiplatform.mpesa.model.Task
import io.piestack.multiplatform.mpesa.model.enums.Environment
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class Mpesa(
    httpClientEngine: HttpClientEngine? = null,
    val appKey: String,
    val appSecret: String,
    val environment: Environment
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
    suspend fun authenticate(): Either<ApiError, AuthResponse> = try {
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

    private fun handleError(exception: Exception): Either<ApiError, Nothing> =
        if (exception is ResponseException) {
            if (exception.response.status.value == 404) {
                Either.Left(ItemNotFoundError)
            } else {
                Either.Left(UnknownError(exception.response.status.value))
            }
        } else {
            Either.Left(NetworkError)
        }
}