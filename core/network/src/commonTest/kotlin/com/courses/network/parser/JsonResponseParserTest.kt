package com.courses.network.parser

import com.courses.network.client.parser.JsonResponseParser
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.js.JsName

@Serializable
data class TestData(
	val id: Int,
	val name: String,
)

class JsonResponseParserTest {
	
	private lateinit var json: Json
	private lateinit var parser: JsonResponseParser
	
	@BeforeTest
	fun setup() {
		json = Json { ignoreUnknownKeys = true }
		parser = JsonResponseParser(json)
	}
	
	@Test
	@JsName("testParseSuccess")
	fun `parse should return object when response is valid`() = runTest {
		// Arrange
		val jsonString = """{"id":1,"name":"Test User"}"""
		val mockEngine = MockEngine { request ->
			respond(
				content = jsonString,
				status = HttpStatusCode.OK,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/test")
		
		val expected = TestData(id = 1, name = "Test User")
		
		// Act
		val result: TestData = parser.parse(response, TestData::class)
		
		// Assert
		assertEquals(expected, result)
	}
	
	@Test
	@JsName("testParseEmptyResponse")
	fun `parse should throw IllegalStateException when response body is empty`() = runTest {
		// Arrange
		val mockEngine = MockEngine { _ ->
			respond(
				content = "",
				status = HttpStatusCode.OK,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/empty")
		
		// Act & Assert
		val exception = assertFailsWith<IllegalStateException> {
			parser.parse(response, TestData::class)
		}
		assertEquals("Empty response body", exception.message)
	}
	
	@Test
	@JsName("testParseInvalidJson")
	fun `parse should throw IllegalStateException when JSON is invalid`() = runTest {
		// Arrange
		val invalidJson = """{"id": "one", "name": "Test User"}""" // id должно быть Int
		val mockEngine = MockEngine { request ->
			respond(
				content = invalidJson,
				status = HttpStatusCode.OK,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/invalid")
		
		// Act & Assert
		val exception = assertFailsWith<IllegalStateException> {
			parser.parse(response, TestData::class)
		}
		
		assertEquals(exception.message?.startsWith("Failed to parse response:"), true)
	}
	
	@Test
	@JsName("testParseWithUnknownKeys")
	fun `parse should ignore unknown keys when ignoreUnknownKeys is true`() = runTest {
		// Arrange
		val jsonString = """{"id":1,"name":"Test User","extra":"value"}"""
		val mockEngine = MockEngine { request ->
			respond(
				content = jsonString,
				status = HttpStatusCode.OK,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/unknown")
		
		val expected = TestData(id = 1, name = "Test User")
		
		// Act
		val result: TestData = parser.parse(response, TestData::class)
		
		// Assert
		assertEquals(expected, result)
	}
	
	@Test
	@JsName("testParseNon200Status")
	fun `parse should handle non-200 HTTP status codes appropriately`() = runTest {
		// Arrange
		val jsonString = """{"id":1,"name":"Test User"}"""
		val mockEngine = MockEngine { _ ->
			respond(
				content = jsonString,
				status = HttpStatusCode.BadRequest,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/badrequest")
		
		val expected = TestData(id = 1, name = "Test User")
		
		// Act
		val result: TestData = parser.parse(response, TestData::class)
		
		// Assert
		assertEquals(expected, result)
	}
	
	@Test
	@JsName("testParseWhitespaceOnlyBody")
	fun `parse should throw IllegalStateException when body contains only whitespace`() = runTest {
		// Arrange
		val whitespaceString = "   \n\t  "
		val mockEngine = MockEngine { request ->
			respond(
				content = whitespaceString,
				status = HttpStatusCode.OK,
				headers = headersOf(HttpHeaders.ContentType, "application/json")
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/whitespace")
		
		// Act & Assert
		val exception = assertFailsWith<IllegalStateException> {
			parser.parse(response, TestData::class)
		}
		assertEquals(true, exception.message?.startsWith("Failed to parse response:") ?: false)
	}
	
	@Test
	@JsName("testParseBodyAsTextThrowsException")
	fun `parse should throw IllegalStateException when bodyAsText throws exception`() = runTest {
		// Arrange
		val mockEngine = MockEngine { _ ->
			respondError(
				status = HttpStatusCode.InternalServerError,
				content = "Internal Server Error"
			)
		}
		val client = HttpClient(mockEngine)
		val response: HttpResponse = client.get("https://example.com/error")
		
		// Act & Assert
		val exception = assertFailsWith<IllegalStateException> {
			parser.parse(response, TestData::class)
		}
		assertEquals(true, exception.message?.startsWith("Failed to parse response:") ?: false)
	}
}