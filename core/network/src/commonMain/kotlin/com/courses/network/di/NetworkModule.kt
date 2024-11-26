package com.courses.network.di

import com.courses.network.client.HttpClient
import com.courses.network.HttpClientFactory
import com.courses.network.client.error.*
import com.courses.network.client.parser.*
import com.courses.network.provider.DefaultHttpClientProvider
import com.courses.network.provider.HttpClientProvider
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule: Module = module {
	single {
		Json {
			prettyPrint = false
			encodeDefaults = true
			isLenient = true
			ignoreUnknownKeys = true
		}
	}
	
	single<ExceptionMapper> { DefaultExceptionMapper() }
	single<ErrorHandler> { DefaultErrorHandler(get()) }
	single<ResponseParser> { JsonResponseParser(get()) }
	single<HttpClientProvider> {
		DefaultHttpClientProvider(
			"jsonplaceholder.typicode.com",
			get()
		)
	}
	
	single<HttpClient> {
		HttpClientFactory.create(
			clientProvider = get(),
			responseParser = get(),
			errorHandler = get(),
		)
	}
}

