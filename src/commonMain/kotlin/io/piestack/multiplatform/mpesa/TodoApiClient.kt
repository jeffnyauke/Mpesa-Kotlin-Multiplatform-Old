package io.piestack.multiplatform.mpesa

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.piestack.multiplatform.mpesa.error.ApiError
import io.piestack.multiplatform.mpesa.error.ItemNotFoundError
import io.piestack.multiplatform.mpesa.error.NetworkError
import io.piestack.multiplatform.mpesa.error.UnknownError
import io.piestack.multiplatform.mpesa.model.AuthResponse
import io.piestack.multiplatform.mpesa.model.Task
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

class TodoApiClient constructor(
    httpClientEngine: HttpClientEngine? = null
) {

    companion object {
        const val BASE_ENDPOINT = "http://jsonplaceholder.typicode.com"
    }

    private val client: HttpClient = HttpClient(httpClientEngine!!) {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                // It's necessary register the serializer because:
                // Obtaining serializer from KClass is not available on native
                // due to the lack of reflection
                register(Task.serializer())
                register(AuthResponse.serializer())
            }
        }
    }

    @UnstableDefault
    suspend fun getAllTasks(): Either<ApiError, List<Task>> = try {
        val tasksJson = client.get<String>("$BASE_ENDPOINT/todos")

        // JsonFeature does not working currently with root-level array
        // https://github.com/Kotlin/kotlinx.serialization/issues/179
        val tasks = Json.nonstrict.parse(Task.serializer().list, tasksJson)

        Either.Right(tasks)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun getTasksById(id: String): Either<ApiError, Task> = try {
        val task = client.get<Task>("$BASE_ENDPOINT/todos/$id")

        Either.Right(task)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun addTask(task: Task): Either<ApiError, Task> = try {
        val taskResponse = client.post<Task>("$BASE_ENDPOINT/todos") {
            contentType(ContentType.Application.Json)
            body = task
        }

        Either.Right(taskResponse)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun updateTask(task: Task): Either<ApiError, Task> = try {
        val taskResponse = client.put<Task>("$BASE_ENDPOINT/todos/${task.id}") {
            contentType(ContentType.Application.Json)
            body = task
        }

        Either.Right(taskResponse)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun deleteTask(id: String): Either<ApiError, Boolean> = try {
        client.delete<String>("$BASE_ENDPOINT/todos/$id")

        Either.Right(true)
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