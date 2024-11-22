package com.courses.client.request

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath

internal class RequestBuilderImpl(
	override var path: String = "",
	override var method: HttpMethod = HttpMethod.GET,
	private val headersMap: MutableMap<String, String> = mutableMapOf(),
	private var requestBody: Any? = null,
) : RequestBuilder {
	fun build(builder: HttpRequestBuilder) {
		builder.url {
			encodedPath = path
		}
		builder.method = when (this.method) {
			HttpMethod.GET -> io.ktor.http.HttpMethod.Get
			HttpMethod.POST -> io.ktor.http.HttpMethod.Post
			HttpMethod.PUT -> io.ktor.http.HttpMethod.Put
			HttpMethod.DELETE -> io.ktor.http.HttpMethod.Delete
			HttpMethod.PATCH -> io.ktor.http.HttpMethod.Patch
			HttpMethod.HEAD -> io.ktor.http.HttpMethod.Head
			HttpMethod.OPTIONS -> io.ktor.http.HttpMethod.Options
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