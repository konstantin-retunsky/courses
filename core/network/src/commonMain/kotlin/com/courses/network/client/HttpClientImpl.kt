package com.courses.network.client

import com.courses.network.client.error.ErrorHandler
import com.courses.network.client.parser.ResponseParser
import com.courses.network.client.request.RequestBuilder
import com.courses.network.client.request.RequestBuilderImpl
import com.courses.network.client.result.NetworkResult
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineDispatcher
import io.ktor.client.HttpClient as KtorHttpClient

internal class HttpClientImpl(
	private val client: KtorHttpClient,
	private val responseParser: ResponseParser,
	private val errorHandler: ErrorHandler,
	private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : HttpClient {
	override suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T> {
		return safeApiCall(dispatcher) {
			val requestBuilder = RequestBuilderImpl().apply(config)
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

