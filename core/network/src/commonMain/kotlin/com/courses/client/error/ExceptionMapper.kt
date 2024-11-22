package com.courses.client.error

import com.courses.client.result.NetworkError

interface ExceptionMapper {
	fun handle(exception: Throwable): NetworkError
}