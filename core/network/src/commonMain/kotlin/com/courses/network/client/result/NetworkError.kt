package com.courses.network.client.result

sealed class NetworkError : Throwable() {
	data object Timeout : NetworkError()
	data object NoInternet : NetworkError()
	data object IoException : NetworkError()
	data class ServerError(val code: Int, val serverError: String?) : NetworkError()
	data class UnknownError(val throwable: Throwable) : NetworkError()
}