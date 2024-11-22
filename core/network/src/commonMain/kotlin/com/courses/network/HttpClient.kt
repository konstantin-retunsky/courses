package com.courses.network

import io.ktor.client.*
import io.ktor.client.HttpClient as KtorHttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

sealed class NetworkResult<out T> {
	data class Success<out T>(val data: T) : NetworkResult<T>()
	data class Error(val error: NetworkError, val message: String? = null) : NetworkResult<Nothing>()
}

sealed class NetworkError : Throwable() {
	data object Timeout : NetworkError()
	data object NoInternet : NetworkError()
	data object IOException : NetworkError()
	data class ServerError(val code: Int, val serverError: String?) : NetworkError()
	data class UnknownError(val throwable: Throwable) : NetworkError()
}

private fun mapExceptionToNetworkError(exception: Throwable): NetworkError {
	return when (exception) {
		is IOException -> NetworkError.IOException
		is TimeoutCancellationException -> NetworkError.Timeout
		is ClientRequestException -> NetworkError.ServerError(
			exception.response.status.value,
			exception.message
		)
		
		is ServerResponseException -> NetworkError.ServerError(
			exception.response.status.value,
			exception.message
		)
		
		else -> NetworkError.UnknownError(exception)
	}
}

enum class HttpMethodType {
	GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
}

class RequestBuilder {
	var path: String = ""
	var method: HttpMethodType = HttpMethodType.GET
	private val headersMap: MutableMap<String, String> = mutableMapOf()
	private var requestBody: Any? = null
	
	fun path(path: String) {
		this.path = path
	}
	
	fun method(method: HttpMethodType) {
		this.method = method
	}
	
	fun headers(headers: Map<String, String>) {
		headersMap.putAll(headers)
	}
	
	fun body(body: Any) {
		requestBody = body
	}
	
	fun applyTo(builder: HttpRequestBuilder) {
		builder.url {
			encodedPath = path
		}
		builder.method = when (this.method) {
			HttpMethodType.GET -> HttpMethod.Get
			HttpMethodType.POST -> HttpMethod.Post
			HttpMethodType.PUT -> HttpMethod.Put
			HttpMethodType.DELETE -> HttpMethod.Delete
			HttpMethodType.PATCH -> HttpMethod.Patch
			HttpMethodType.HEAD -> HttpMethod.Head
			HttpMethodType.OPTIONS -> HttpMethod.Options
		}
		builder.headers {
			headersMap.forEach { (key, value) ->
				append(key, value)
			}
		}
		requestBody?.let {
			builder.contentType(ContentType.Application.Json)
			builder.setBody(it)
		}
	}
}


class HttpClient(
	val client: KtorHttpClient = HttpClientProvider.client,
) {
	suspend inline fun <reified T> request(
		crossinline config: RequestBuilder.() -> Unit,
	): NetworkResult<T> {
		return safeApiCall<T> {
			val requestBuilder = RequestBuilder().apply(config)
			
			val response: HttpResponse = client.request {
				requestBuilder.applyTo(this)
			}
			
			parseResponse(response)
		}
	}
	
	suspend inline fun <reified T> parseResponse(response: HttpResponse): T {
		return response.body<T>()
	}
	
	suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
		return try {
			val response = withContext(Dispatchers.Default) { apiCall() }
			NetworkResult.Success(response)
		} catch (e: Throwable) {
			val networkError = mapExceptionToNetworkError(e)
			NetworkResult.Error(networkError, networkError.toString())
		}
	}
}

private object HttpClientProvider {
	val client: KtorHttpClient by lazy {
		KtorHttpClient {
			install(Logging) {
				level = LogLevel.BODY
			}
			install(ContentNegotiation) {
				json(Json {
					prettyPrint = false // Отключить форматирование
					encodeDefaults = true // Кодировать значения по умолчанию
					
					isLenient = true // Разрешить нестрогий синтаксис
					ignoreUnknownKeys = true // Игнорировать неизвестные поля в JSON
				})
			}
			defaultRequest {
				url {
					protocol = URLProtocol.HTTPS
					host = "jsonplaceholder.typicode.com" // Установите хост JSONPlaceholder
				}
				headers.append("Accept", "application/json")
			}
		}
	}
}
