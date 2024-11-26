package com.courses.client.error

import com.courses.client.result.NetworkError
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException

class DefaultExceptionMapper : ExceptionMapper {
	override fun handle(exception: Throwable): NetworkError {
		return when (exception) {
			is IOException -> NetworkError.IoException
			is TimeoutCancellationException -> NetworkError.Timeout
			is ResponseException -> {
				val code = exception.response.status.value
				NetworkError.ServerError(code, exception.message ?: "Server Error")
			}
			
			else -> NetworkError.UnknownError(exception)
		}
	}
}