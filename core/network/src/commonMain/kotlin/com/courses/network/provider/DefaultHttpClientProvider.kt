package com.courses.network.provider

import io.ktor.client.HttpClient as KtorHttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class DefaultHttpClientProvider(
	private val baseUrl: String,
	private val json: Json,
) : HttpClientProvider {
	override val client = KtorHttpClient {
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