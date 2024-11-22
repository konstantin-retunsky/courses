package com.courses.client.error

import com.courses.client.result.NetworkError

class DefaultErrorHandler(private val exceptionMapper: ExceptionMapper) : ErrorHandler {
	override fun handle(exception: Throwable): NetworkError {
		return exceptionMapper.handle(exception)
	}
}