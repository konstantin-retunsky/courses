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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

sealed class NetworkResult<out T> {
	data class Success<out T>(val data: T) : NetworkResult<T>()
	data class Error(val error: NetworkError, val message: String? = null) : NetworkResult<Nothing>()
}

sealed class NetworkError : Throwable() {
	object Timeout : NetworkError()
	object NoInternet : NetworkError()
	object IoException : NetworkError()
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

interface RequestBuilder {
	var path: String
	var method: HttpMethodType
	fun headers(headers: Map<String, String>)
	fun body(body: Any)
}

class JsonRequestBuilder(
	override var path: String = "",
	override var method: HttpMethodType = HttpMethodType.GET,
	private val headersMap: MutableMap<String, String> = mutableMapOf(),
	private var requestBody: Any? = null,
) : RequestBuilder {
	
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
	
	override fun headers(headers: Map<String, String>) {
		headersMap.putAll(headers)
	}
	
	override fun body(body: Any) {
		requestBody = body
	}
}

interface ResponseParser {
	suspend fun <T : Any> parse(response: HttpResponse, responseClass: KClass<T>): T
}

class JsonResponseParser(private val json: Json) : ResponseParser {
	@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
	override suspend fun <T : Any> parse(response: HttpResponse, responseClass: KClass<T>): T {
		val responseBody = response.body<String>()
		val deserializationStrategy: DeserializationStrategy<T> =
			json.serializersModule.getContextual(responseClass) ?: responseClass.serializer()
		
		return json.decodeFromString(deserializationStrategy, responseBody)
	}
}

interface ErrorHandler {
	fun handle(exception: Throwable): NetworkError
}

class DefaultErrorHandler(private val exceptionMapper: ExceptionMapper) : ErrorHandler {
	override fun handle(exception: Throwable): NetworkError {
		return exceptionMapper.map(exception)
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
	private val responseParser: ResponseParser,
	private val errorHandler: ErrorHandler,
	private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : HttpClient {
	
	override suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T> {
		return safeApiCall(dispatcher) {
			val requestBuilder = JsonRequestBuilder().apply(config)
			val response: HttpResponse = client.request {
				requestBuilder.build(this)
			}
			responseParser.parse(response, responseClass)
		}
	}
	
	private suspend fun <T> safeApiCall(
		dispatcher: CoroutineDispatcher,
		apiCall: suspend () -> T,
	): NetworkResult<T> {
		return try {
			val response = withContext(dispatcher) { apiCall() }
			NetworkResult.Success(response)
		} catch (e: Throwable) {
			val networkError = errorHandler.handle(e)
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

class DefaultHttpClientProvider(
	private val baseUrl: String,
	private val json: Json,
) : HttpClientProvider {
	override val client: io.ktor.client.HttpClient by lazy {
		HttpClient {
			install(Logging) {
				level = LogLevel.BODY
			}
			install(ContentNegotiation) {
				json(json)
			}
			defaultRequest {
				url {
					protocol = URLProtocol.HTTPS
					host = baseUrl
				}
				headers.append("Accept", "application/json")
			}
		}
	}
}

object HttpClientFactory {
	fun create(
		clientProvider: HttpClientProvider,
		responseParser: ResponseParser,
		errorHandler: ErrorHandler,
	): HttpClient {
		return HttpClientImpl(
			client = clientProvider.client,
			responseParser = responseParser,
			errorHandler = errorHandler
		)
	}
}

class NetworkModule(
	private val baseUrl: String,
	private val json: Json = Json {
		prettyPrint = false
		encodeDefaults = true
		isLenient = true
		ignoreUnknownKeys = true
	},
) {
	val exceptionMapper: ExceptionMapper = DefaultExceptionMapper()
	val errorHandler: ErrorHandler = DefaultErrorHandler(exceptionMapper)
	val responseParser: ResponseParser = JsonResponseParser(json)
	val httpClientProvider: HttpClientProvider = DefaultHttpClientProvider(baseUrl, json)
	val networkClient: HttpClient = HttpClientFactory.create(
		clientProvider = httpClientProvider,
		responseParser = responseParser,
		errorHandler = errorHandler
	)
}

fun provideNetworkClient(): HttpClient {
	val networkModule = NetworkModule(baseUrl = "jsonplaceholder.typicode.com")
	return networkModule.networkClient
}