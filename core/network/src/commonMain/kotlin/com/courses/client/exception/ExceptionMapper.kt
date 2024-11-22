package com.courses.client.exception

import com.courses.client.result.NetworkError

interface ExceptionMapper {
	fun map(exception: Throwable): NetworkError
}