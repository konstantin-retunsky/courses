package com.courses.client.request

import com.courses.client.model.Post
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpMethod as KtorHttpMethod
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RequestBuilderTest {
	companion object {
		private const val BASE_URL = "https://jsonplaceholder.typicode.com"
	}
	
	private lateinit var requestBuilder: RequestBuilderImpl
	
	@BeforeTest
	fun setUp() {
		requestBuilder = RequestBuilderImpl()
	}
	
	@Test
	@JsName("test_GET_request_is_built_correctly")
	fun `GET request is built correctly`() {
		// Given
		val expectedPath = "$BASE_URL/posts/1"
		
		// When
		requestBuilder.apply {
			method = HttpMethod.GET
			url = expectedPath
		}
		val httpRequestBuilder = HttpRequestBuilder()
		requestBuilder.build(httpRequestBuilder)
		
		// Then
		assertEquals(KtorHttpMethod.Get, httpRequestBuilder.method, "HTTP method should be GET")
		assertEquals(
			expectedPath,
			httpRequestBuilder.url.toString(),
			"URL should match the expected path"
		)
	}
	
	@Test
	@JsName("test_POST_request_with_body_is_built_correctly")
	fun `POST request with body is built correctly`() {
		// Given
		val post = Post(
			id = 1,
			userId = 1,
			title = "Test Post",
			body = "This is a test post"
		)
		val expectedPath = "$BASE_URL/posts"
		
		// When
		requestBuilder.apply {
			method = HttpMethod.POST
			url = expectedPath
			body(post)
		}
		val httpRequestBuilder = HttpRequestBuilder()
		requestBuilder.build(httpRequestBuilder)
		
		// Then
		assertEquals(KtorHttpMethod.Post, httpRequestBuilder.method, "HTTP method should be POST")
		assertEquals(
			expectedPath,
			httpRequestBuilder.url.toString(),
			"URL should match the expected path"
		)
		assertNotNull(httpRequestBuilder.body, "Request body should not be null")
		assertEquals(post, httpRequestBuilder.body, "Request body should match the provided post")
	}
	
	@Test
	@JsName("test_GET_request_with_headers_is_built_correctly")
	fun `GET request with headers is built correctly`() {
		// Given
		val headers = mapOf(
			"Authorization" to "Bearer token",
			"Content-Type" to "application/json"
		)
		val expectedPath = "$BASE_URL/posts"
		
		// When
		requestBuilder.apply {
			method = HttpMethod.GET
			url = expectedPath
			headers(headers)
		}
		val httpRequestBuilder = HttpRequestBuilder()
		requestBuilder.build(httpRequestBuilder)
		
		// Then
		assertEquals(KtorHttpMethod.Get, httpRequestBuilder.method, "HTTP method should be GET")
		assertEquals(
			expectedPath,
			httpRequestBuilder.url.toString(),
			"URL should match the expected path"
		)
		headers.forEach { (key, value) ->
			assertTrue(httpRequestBuilder.headers.contains(key), "Header '$key' should be present")
			assertEquals(
				value,
				httpRequestBuilder.headers[key],
				"Header '$key' should have the correct value"
			)
		}
	}
}