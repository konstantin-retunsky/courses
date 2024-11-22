package com.courses.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

sealed class NetworkResult<out T> {
	data class Success<out T>(val data: T) : NetworkResult<T>()
	data class Error(val error: NetworkError, val message: String? = null) : NetworkResult<Nothing>()
}

sealed class NetworkError : Throwable() {
	data object Timeout : NetworkError()
	data object NoInternet : NetworkError()
	data object IoException : NetworkError()
	data class ServerError(val code: Int, val serverError: String?) : NetworkError()
	data class UnknownError(val throwable: Throwable) : NetworkError()
}

interface ExceptionMapper {
	fun map(exception: Throwable): NetworkError
}

class DefaultExceptionMapper : ExceptionMapper {
	override fun map(exception: Throwable): NetworkError {
		return when (exception) {
			is IOException -> NetworkError.IoException
			is TimeoutCancellationException -> NetworkError.Timeout
			is ClientRequestException, is ServerResponseException -> {
				val code = (exception as? ClientRequestException)?.response?.status?.value
					?: (exception as? ServerResponseException)?.response?.status?.value ?: -1
				NetworkError.ServerError(code, exception.message)
			}
			
			else -> NetworkError.UnknownError(exception)
		}
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
	
	fun build(builder: HttpRequestBuilder) {
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

interface HttpClient {
	suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T>
}

class HttpClientImpl(
	private val client: io.ktor.client.HttpClient,
	private val exceptionMapper: ExceptionMapper,
	private val json: Json,
) : HttpClient {
	
	override suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T> {
		return safeApiCall {
			val requestBuilder = RequestBuilder().apply(config)
			val response: HttpResponse = client.request {
				requestBuilder.build(this)
			}
			parseResponse(response, responseClass)
		}
	}
	
	@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
	private suspend fun <T : Any> parseResponse(response: HttpResponse, responseClass: KClass<T>): T {
		val responseBody = response.body<String>()
		val deserializationStrategy: DeserializationStrategy<T> =
			json.serializersModule.getContextual(responseClass) ?: responseClass.serializer()
		
		return json.decodeFromString(deserializationStrategy, responseBody)
	}
	
	private suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
		return try {
			val response = withContext(Dispatchers.Default) { apiCall() }
			NetworkResult.Success(response)
		} catch (e: Throwable) {
			val networkError = exceptionMapper.map(e)
			NetworkResult.Error(networkError, networkError.toString())
		}
	}
}

suspend inline fun <reified T : Any> HttpClient.request(
	noinline config: RequestBuilder.() -> Unit,
): NetworkResult<T> {
	return this.request(config, T::class)
}

interface HttpClientProvider {
	val client: io.ktor.client.HttpClient
}

class DefaultHttpClientProvider : HttpClientProvider {
	override val client: io.ktor.client.HttpClient by lazy {
		HttpClient {
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
					host = "jsonplaceholder.typicode.com"
				}
				headers.append("Accept", "application/json")
			}
		}
	}
}

object ServiceLocator {
	private val httpClientProvider: HttpClientProvider = DefaultHttpClientProvider()
	private val exceptionMapper: ExceptionMapper = DefaultExceptionMapper()
	private val json: Json = Json {
		prettyPrint = false
		encodeDefaults = true
		isLenient = true
		ignoreUnknownKeys = true
	}
	
	val networkClient: HttpClient = HttpClientImpl(httpClientProvider.client, exceptionMapper, json)
}

