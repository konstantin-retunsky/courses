package com.courses.network

import com.courses.network.client.HttpClient
import com.courses.network.client.HttpClientImpl
import com.courses.network.client.error.ErrorHandler
import com.courses.network.client.parser.ResponseParser
import com.courses.network.provider.HttpClientProvider

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