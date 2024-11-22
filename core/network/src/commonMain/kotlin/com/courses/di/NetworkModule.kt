package com.courses.di

import com.courses.client.HttpClient
import com.courses.client.HttpClientImpl
import com.courses.client.error.DefaultErrorHandler
import com.courses.client.error.ErrorHandler
import com.courses.client.error.DefaultExceptionMapper
import com.courses.client.error.ExceptionMapper
import com.courses.client.parser.JsonResponseParser
import com.courses.client.parser.ResponseParser
import io.ktor.client.HttpClient as KtorHttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NetworkModule(
	private val baseUrl: String,
	private val json: Json = Json {
		prettyPrint = false
		encodeDefaults = true
		isLenient = true
		ignoreUnknownKeys = true
	},
) {
	private val exceptionMapper: ExceptionMapper = DefaultExceptionMapper()
	private val errorHandler: ErrorHandler = DefaultErrorHandler(exceptionMapper)
	private val responseParser: ResponseParser = JsonResponseParser(json)
	private val httpClientProvider: HttpClientProvider = DefaultHttpClientProvider(baseUrl, json)
	
	val networkClient: HttpClient = HttpClientFactory.create(
		clientProvider = httpClientProvider,
		responseParser = responseParser,
		errorHandler = errorHandler
	)
}

interface HttpClientProvider {
	val client: io.ktor.client.HttpClient
}

class DefaultHttpClientProvider(
	private val baseUrl: String,
	private val json: Json,
) : HttpClientProvider {
	override val client: io.ktor.client.HttpClient by lazy {
		KtorHttpClient {
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