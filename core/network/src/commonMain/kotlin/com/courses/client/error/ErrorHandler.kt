package com.courses.client.error

import com.courses.client.result.NetworkError

interface ErrorHandler {
	fun handle(exception: Throwable): NetworkError
}