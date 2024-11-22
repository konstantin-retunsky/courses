package com.courses.client.exception

import com.courses.client.result.NetworkError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException

class DefaultExceptionMapper : ExceptionMapper {
	override fun map(exception: Throwable): NetworkError {
		return when (exception) {
			is IOException -> NetworkError.IoException
			is TimeoutCancellationException -> NetworkError.Timeout
			is ClientRequestException, is ServerResponseException -> {
				val code = (exception as? ClientRequestException)?.response?.status?.value
					?: (exception as? ServerResponseException)?.response?.status?.value ?: -1
				NetworkError.ServerError(code, exception.message)
			}
			
			else -> NetworkError.UnknownError(exception)
		}
	}
}