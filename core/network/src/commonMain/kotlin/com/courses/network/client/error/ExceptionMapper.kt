package com.courses.network.client.error

import com.courses.network.client.result.NetworkError

interface ExceptionMapper {
	fun handle(exception: Throwable): NetworkError
}