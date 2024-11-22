package com.courses.client.result

sealed class NetworkResult<out T> {
	data class Success<out T>(val data: T) : NetworkResult<T>()
	data class Error(val error: NetworkError, val message: String? = null) : NetworkResult<Nothing>()
}