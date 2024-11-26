package com.courses.network.provider

import io.ktor.client.HttpClient as KtorHttpClient

interface HttpClientProvider {
	val client: KtorHttpClient
}