package com.courses.network.client.extension

import com.courses.network.client.HttpClient
import com.courses.network.client.request.RequestBuilder
import com.courses.network.client.result.NetworkResult

suspend inline fun <reified T : Any> HttpClient.request(
	noinline config: RequestBuilder.() -> Unit,
): NetworkResult<T> {
	return this.request(config, T::class)
}