package com.courses.client

import com.courses.client.extension.request
import com.courses.client.error.ErrorHandler
import com.courses.client.parser.ResponseParser
import com.courses.client.result.NetworkError
import com.courses.client.result.NetworkResult
import com.courses.client.request.HttpMethod
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.mock
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.matcher.matching
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.ktor.client.HttpClient as KtorHttpClient


@Serializable
data class TestData(val id: Int, val name: String)

class HttpClientImplTest {
	
	private lateinit var responseParser: ResponseParser
	private lateinit var errorHandler: ErrorHandler
	
	@BeforeTest
	fun setup() {
		responseParser = mock()
		errorHandler = mock()
	}
	
	/**
	 * Вспомогательный метод для создания HttpClientImpl с MockEngine
	 */
	private fun createClientWithMockEngine(
		responseContent: String,
		status: HttpStatusCode = HttpStatusCode.OK,
	): HttpClientImpl {
		val mockEngine = MockEngine { _ ->
			respond(
				content = responseContent,
				status = status,
				headers = headersOf(HttpHeaders.ContentType, "application/json; charset=utf-8")
			)
		}
		val httpClient = KtorHttpClient(mockEngine)
		
		return HttpClientImpl(
			client = httpClient,
			responseParser = responseParser,
			errorHandler = errorHandler
		)
	}
	
	@Test
	@JsName("testSuccessfulRequest")
	fun `request should return NetworkResult_Success when API call is successful`() = runTest {
		// Arrange
		val jsonString = """{"id":1,"name":"Test User"}"""
		val expectedData = TestData(id = 1, name = "Test User")
		
		val clientImpl = createClientWithMockEngine(jsonString)
		
		everySuspend { responseParser.parse(any(), TestData::class) } returns expectedData
		
		// Act
		val result: NetworkResult<TestData> = clientImpl.request<TestData> {
			method = HttpMethod.GET
			url = "https://example.com/test"
		}
		
		// Assert
		assertTrue(result is NetworkResult.Success)
		assertEquals(expectedData, result.data)
		
		verifySuspend(exactly(1)) {
			responseParser.parse(any(), TestData::class)
		}
	}
	
	@Test
	@JsName("testEmptyResponse")
	fun `request should return NetworkResult_Error when response body is empty`() = runTest {
		// Arrange
		val msgErr = "Empty response body"
		val emptyResponse = ""
		val clientImpl = createClientWithMockEngine(emptyResponse)
		val exception = IllegalStateException(msgErr)
		
		everySuspend { responseParser.parse(any(), TestData::class) } throws exception
		
		everySuspend {
			errorHandler.handle(matching { it is IllegalStateException && it.message == msgErr })
		} returns NetworkError.UnknownError(exception)
		
		
		// Act
		val result: NetworkResult<TestData> = clientImpl.request<TestData> {
			method = HttpMethod.GET
			url = "https://example.com/empty"
		}
		
		// Assert
		assertTrue(result is NetworkResult.Error)
		
		verifySuspend(exactly(1)) {
			responseParser.parse(any(), TestData::class)
			errorHandler.handle(matching { it is IllegalStateException && it.message == msgErr })
		}
	}
	
	@Test
	@JsName("testInvalidJsonResponse")
	fun `request should return NetworkResult_Error when JSON is invalid`() = runTest {
		// Arrange
		val invalidJson = """{"id": "one", "name": "Test User"}""" // id должно быть Int
		val clientImpl = createClientWithMockEngine(invalidJson)
		val exception =
			IllegalStateException("Failed to parse response: kotlinx.serialization.SerializationException: ...")
		
		everySuspend { responseParser.parse(any(), TestData::class) } throws exception
		everySuspend {
			errorHandler.handle(matching { it is IllegalStateException && it.message?.contains("Failed to parse response") == true })
		} returns NetworkError.UnknownError(exception)
		
		// Act
		val result: NetworkResult<TestData> = clientImpl.request<TestData> {
			method = HttpMethod.GET
			url = "https://example.com/invalid"
		}
		
		// Assert
		assertTrue(result is NetworkResult.Error)
		
		verifySuspend(exactly(1)) {
			responseParser.parse(any(), TestData::class)
			errorHandler.handle(matching { it is IllegalStateException && it.message?.contains("Failed to parse response") == true })
		}
	}
	
	@Test
	@JsName("testExceptionDuringRequest")
	fun `request should return NetworkResult_Error when an exception occurs during the request`() =
		runTest {
			// Arrange
			val mockEngine = MockEngine { _ ->
				throw IOException("Network error")
			}
			val httpClient = KtorHttpClient(mockEngine)
			val clientImpl = HttpClientImpl(
				client = httpClient,
				responseParser = responseParser,
				errorHandler = errorHandler
			)
			val exception = IOException("Network error")
			
			everySuspend {
				errorHandler.handle(matching {
					it is IOException && it.message == "Network error"
				})
			} returns NetworkError.UnknownError(exception)
			
			// Act
			val result: NetworkResult<TestData> = clientImpl.request<TestData> {
				method = HttpMethod.GET
				url = "https://example.com/networkerror"
			}
			
			// Assert
			assertTrue(result is NetworkResult.Error)
			
			verifySuspend(exactly(1)) {
				errorHandler.handle(matching { it is IOException && it.message == "Network error" })
			}
		}
}