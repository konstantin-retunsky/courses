package com.courses.network.client.error

import com.courses.network.client.result.NetworkError

interface ErrorHandler {
	fun handle(exception: Throwable): NetworkError
}