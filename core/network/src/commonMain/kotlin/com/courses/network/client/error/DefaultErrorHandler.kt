package com.courses.network.client.error

import com.courses.network.client.result.NetworkError

internal class DefaultErrorHandler(private val exceptionMapper: ExceptionMapper) : ErrorHandler {
	override fun handle(exception: Throwable): NetworkError {
		return exceptionMapper.handle(exception)
	}
}