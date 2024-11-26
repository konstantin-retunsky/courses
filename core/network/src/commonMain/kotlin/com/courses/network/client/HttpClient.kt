package com.courses.network.client

import com.courses.network.client.request.RequestBuilder
import com.courses.network.client.result.NetworkResult
import kotlin.reflect.KClass

interface RequestExecutor {
	suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T>
}


interface HttpClient : RequestExecutor {
	override suspend fun <T : Any> request(
		config: RequestBuilder.() -> Unit,
		responseClass: KClass<T>,
	): NetworkResult<T>
}
