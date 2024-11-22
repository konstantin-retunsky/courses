package com.courses.client.parser

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class JsonResponseParser(private val json: Json) : ResponseParser {
	@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
	override suspend fun <T : Any> parse(response: HttpResponse, responseClass: KClass<T>): T {
		val responseBody = response.body<String>()
		val deserializationStrategy: DeserializationStrategy<T> =
			json.serializersModule.getContextual(responseClass) ?: responseClass.serializer()
		
		return json.decodeFromString(deserializationStrategy, responseBody)
	}
}