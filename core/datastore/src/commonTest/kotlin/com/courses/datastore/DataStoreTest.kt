package com.courses.datastore

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.js.JsName
import kotlin.test.*

@Serializable
data class User(val id: Int, val name: String)

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreImplTest {
	private lateinit var dataStore: DataStore
	
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)
	
	@BeforeTest
	fun setUp() {
		Dispatchers.resetMain()
		Dispatchers.setMain(testDispatcher)
		
		dataStore = DataStoreImpl(MapSettings())
	}
	
	@AfterTest
	fun tearDown() {
		Dispatchers.resetMain()
		testScope.cancel()
	}
	
	@Test
	@JsName("testSetAndGetInt")
	fun `set and get Int`() = testScope.runTest {
		val key = "intKey"
		val value = 42
		dataStore.set(key, value)
		val retrievedValue = dataStore.getInt(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetLong")
	fun `set and get Long`() = testScope.runTest {
		val key = "longKey"
		val value = 123456789L
		dataStore.set(key, value)
		val retrievedValue = dataStore.getLong(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetString")
	fun `set and get String`() = testScope.runTest {
		val key = "stringKey"
		val value = "Hello, DataStore!"
		dataStore.set(key, value)
		val retrievedValue = dataStore.getString(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetBoolean")
	fun `set and get Boolean`() = testScope.runTest {
		val key = "booleanKey"
		val value = true
		dataStore.set(key, value)
		val retrievedValue = dataStore.getBoolean(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetFloat")
	fun `set and get Float`() = testScope.runTest {
		val key = "floatKey"
		val value = 3.14f
		dataStore.set(key, value)
		val retrievedValue = dataStore.getFloat(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetCustomObject")
	fun `set and get Custom Object`() = testScope.runTest {
		val key = "userKey"
		val user = User(1, "John Doe")
		val serializer = serializer<User>()
		dataStore.set(key, user, serializer)
		val retrievedUser = dataStore.get(key, serializer)
		assertEquals(user, retrievedUser)
	}
	
	@Test
	@JsName("testGetIntOrNull")
	fun `get Int or null`() = testScope.runTest {
		val key = "intOrNullKey"
		assertNull(dataStore.getIntOrNull(key))
		
		dataStore.set(key, 100)
		val retrievedValue = dataStore.getIntOrNull(key)
		assertEquals(100, retrievedValue)
	}
	
	@Test
	@JsName("testGetLongOrNull")
	fun `get Long or null`() = testScope.runTest {
		val key = "longOrNullKey"
		assertNull(dataStore.getLongOrNull(key))
		
		dataStore.set(key, 987654321L)
		val retrievedValue = dataStore.getLongOrNull(key)
		assertEquals(987654321L, retrievedValue)
	}
	
	@Test
	@JsName("testGetStringOrNull")
	fun `get String or null`() = testScope.runTest {
		val key = "stringOrNullKey"
		assertNull(dataStore.getStringOrNull(key))
		
		dataStore.set(key, "Nullable String")
		val retrievedValue = dataStore.getStringOrNull(key)
		assertEquals("Nullable String", retrievedValue)
	}
	
	@Test
	@JsName("testGetBooleanOrNull")
	fun `get Boolean or null`() = testScope.runTest {
		val key = "booleanOrNullKey"
		assertNull(dataStore.getBooleanOrNull(key))
		
		dataStore.set(key, true)
		val retrievedValue = dataStore.getBooleanOrNull(key)
		assertTrue(retrievedValue!!)
	}
	
	@Test
	@JsName("testGetFloatOrNull")
	fun `get Float or null`() = testScope.runTest {
		val key = "floatOrNullKey"
		assertNull(dataStore.getFloatOrNull(key))
		
		dataStore.set(key, 2.718f)
		val retrievedValue = dataStore.getFloatOrNull(key)
		assertEquals(2.718f, retrievedValue)
	}
	
	@Test
	@JsName("testGetIntFlow")
	fun `get Int Flow`() = testScope.runTest {
		val key = "intFlowKey"
		val defaultValue = 10
		val newValue = 20
		val flow = dataStore.getIntFlow(key, defaultValue)
		
		flow.test {
			assertEquals(defaultValue, awaitItem())
			
			dataStore.set(key, newValue)
			
			assertEquals(newValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testGetStringFlow")
	fun `get String Flow`() = testScope.runTest {
		val key = "stringFlowKey"
		val defaultValue = "Default"
		val newValue = "New Value"
		val flow = dataStore.getStringFlow(key, defaultValue)
		
		flow.test {
			assertEquals(defaultValue, awaitItem())
			
			dataStore.set(key, newValue)
			
			assertEquals(newValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testGetBooleanFlow")
	fun `get Boolean Flow`() = testScope.runTest {
		val key = "booleanFlowKey"
		val defaultValue = false
		val newValue = true
		val flow = dataStore.getBooleanFlow(key, defaultValue)
		
		flow.test {
			assertEquals(defaultValue, awaitItem())
			
			dataStore.set(key, newValue)
			
			assertEquals(newValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testGetFloatFlow")
	fun `get Float Flow`() = testScope.runTest {
		val key = "floatFlowKey"
		val defaultValue = 1.23f
		val newValue = 4.56f
		val flow = dataStore.getFloatFlow(key, defaultValue)
		
		flow.test {
			assertEquals(defaultValue, awaitItem())
			
			dataStore.set(key, newValue)
			
			assertEquals(newValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testGetLongFlow")
	fun `get Long Flow`() = testScope.runTest {
		val key = "longFlowKey"
		val defaultValue = 1000L
		val newValue = 2000L
		val flow = dataStore.getLongFlow(key, defaultValue)
		
		flow.test {
			assertEquals(defaultValue, awaitItem())
			
			dataStore.set(key, newValue)
			
			assertEquals(newValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testGetCustomObjectFlow")
	fun `get Custom Object Flow`() = testScope.runTest {
		val key = "userFlowKey"
		val serializer = serializer<User>()
		val flow = dataStore.getFlow(key, serializer)
		
		flow.test {
			assertNull(awaitItem())
			
			val user = User(2, "Jane Smith")
			dataStore.set(key, user, serializer)
			
			assertEquals(user, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
	
	@Test
	@JsName("testRemoveKey")
	fun `remove key`() = testScope.runTest {
		val key = "removeKey"
		val value = "To be removed"
		
		dataStore.set(key, value)
		dataStore.remove(key)
		
		val retrievedValue = dataStore.getStringOrNull(key)
		
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testRemoveNonExistentKey")
	fun `remove non-existent key`() = testScope.runTest {
		val key = "nonExistentKey"
		dataStore.remove(key)
		assertNull(dataStore.getStringOrNull(key))
	}
	
	@Test
	@JsName("testClearAll")
	fun `clear all keys`() = testScope.runTest {
		val key1 = "key1"
		val key2 = "key2"
		val key3 = "key3"
		
		dataStore.set(key1, "Value1")
		dataStore.set(key2, 123)
		dataStore.set(key3, true)
		
		dataStore.clearAll()
		
		assertNull(dataStore.getStringOrNull(key1))
		assertNull(dataStore.getIntOrNull(key2))
		assertNull(dataStore.getBooleanOrNull(key3))
	}
	
	@Test
	@JsName("testSetNullValue")
	fun `set null value`() = testScope.runTest {
		val key = "nullValueKey"
		dataStore.set<String>(key, null, null)
		val retrievedValue = dataStore.getStringOrNull(key)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testSetNullCustomObject")
	fun `set null Custom Object`() = testScope.runTest {
		val key = "nullUserKey"
		val serializer = serializer<User>()
		dataStore.set<User>(key, null, serializer)
		val retrievedUser = dataStore.get<User>(key, serializer)
		assertNull(retrievedUser)
	}
	
	@Test
	@JsName("testGetWithInvalidSerializer")
	fun `get with invalid serializer`() = testScope.runTest {
		val key = "invalidSerializerKey"
		val user = User(3, "Invalid Serializer")
		val correctSerializer = serializer<User>()
		dataStore.set(key, user, correctSerializer)
		
		val wrongSerializer = serializer<String>()
		
		val retrievedValue = dataStore.get(key, wrongSerializer)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testSetWithInvalidSerializer")
	fun `set with invalid serializer`() = testScope.runTest {
		val key = "invalidSetSerializerKey"
		val invalidValue = "Invalid Set Serializer"
		val correctSerializer = serializer<User>()
		
		try {
			dataStore.set(key, invalidValue)
		} catch (e: Exception) {
			// Ожидаемое поведение при использовании неверного сериализатора
			// Можно дополнительно проверить тип исключения, если необходимо
		}
		
		val retrievedValue = dataStore.get(key, correctSerializer)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testGetWithDefaultValues")
	fun `get with default values`() = testScope.runTest {
		val intKey = "defaultIntKey"
		val longKey = "defaultLongKey"
		val stringKey = "defaultStringKey"
		val booleanKey = "defaultBooleanKey"
		val floatKey = "defaultFloatKey"
		
		assertEquals(10, dataStore.getInt(intKey, 10))
		assertEquals(1000L, dataStore.getLong(longKey, 1000L))
		assertEquals("Default", dataStore.getString(stringKey, "Default"))
		assertEquals(true, dataStore.getBoolean(booleanKey, true))
		assertEquals(3.14f, dataStore.getFloat(floatKey, 3.14f))
	}
	
	@Test
	@JsName("testGetNonExistentKeys")
	fun `get non-existent keys`() = testScope.runTest {
		assertNull(dataStore.getIntOrNull("nonExistentInt"))
		assertNull(dataStore.getLongOrNull("nonExistentLong"))
		assertNull(dataStore.getStringOrNull("nonExistentString"))
		assertNull(dataStore.getBooleanOrNull("nonExistentBoolean"))
		assertNull(dataStore.getFloatOrNull("nonExistentFloat"))
	}
	
	@Test
	@JsName("testFlowWithTurbine")
	fun `flow with Turbine`() = testScope.runTest {
		val key = "reactiveKey"
		val initialValue = "Initial"
		val updatedValue = "First Update"
		
		dataStore.set(key, initialValue)
		
		val flow = dataStore.getStringFlow(key, initialValue)
		
		flow.test {
			assertEquals(initialValue, awaitItem())
			
			dataStore.set(key, updatedValue)
			
			assertEquals(updatedValue, awaitItem())
			
			dataStore.remove(key)
			
			assertEquals(initialValue, awaitItem())
			
			cancelAndIgnoreRemainingEvents()
		}
	}
}

