package com.courses.client.parser

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class JsonResponseParser(private val json: Json) : ResponseParser {
	@OptIn(InternalSerializationApi::class)
	override suspend fun <T : Any> parse(response: HttpResponse, responseClass: KClass<T>): T {
		val responseBody = response.bodyAsText()
		if (responseBody.isEmpty()) {
			throw IllegalStateException("Empty response body")
		}
		
		return try {
			val serializer = responseClass.serializer()
			json.decodeFromString(serializer, responseBody)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to parse response: ${e.message}", e)
		}
	}
}