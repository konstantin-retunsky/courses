package com.courses.network.client.parser

import io.ktor.client.statement.HttpResponse
import kotlin.reflect.KClass

interface ResponseParser {
	suspend fun <T : Any> parse(response: HttpResponse, responseClass: KClass<T>): T
}