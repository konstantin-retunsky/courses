package com.courses.client.extension

import com.courses.client.HttpClient
import com.courses.client.request.RequestBuilder
import com.courses.client.result.NetworkResult

suspend inline fun <reified T : Any> HttpClient.request(
	noinline config: RequestBuilder.() -> Unit,
): NetworkResult<T> {
	return this.request(config, T::class)
}