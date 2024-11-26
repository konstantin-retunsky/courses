package com.courses.client.request

interface RequestBuilder {
	var url: String
	var method: HttpMethod
	fun headers(headers: Map<String, String>)
	fun body(body: Any)
}