package com.courses.datastore

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Serializable
data class User(val id: Int, val name: String)

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreImplTest {
	
	private lateinit var mapSettings: MapSettings
	private lateinit var observableSettings: ObservableSettings
	private lateinit var dataStore: DataStore
	
	@BeforeTest
	fun setup() {
		mapSettings = MapSettings()
		observableSettings = mapSettings
		dataStore = DataStoreImpl(observableSettings)
	}
	
	@Test
	@JsName("testSetAndGetInt")
	fun `set and get Int`() = runTest {
		val key = "intKey"
		val value = 42
		dataStore.set(key, value)
		val retrievedValue = dataStore.getInt(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetLong")
	fun `set and get Long`() = runTest {
		val key = "longKey"
		val value = 123456789L
		dataStore.set(key, value)
		val retrievedValue = dataStore.getLong(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetString")
	fun `set and get String`() = runTest {
		val key = "stringKey"
		val value = "Hello, DataStore!"
		dataStore.set(key, value)
		val retrievedValue = dataStore.getString(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetBoolean")
	fun `set and get Boolean`() = runTest {
		val key = "booleanKey"
		val value = true
		dataStore.set(key, value)
		val retrievedValue = dataStore.getBoolean(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetFloat")
	fun `set and get Float`() = runTest {
		val key = "floatKey"
		val value = 3.14f
		dataStore.set(key, value)
		val retrievedValue = dataStore.getFloat(key)
		assertEquals(value, retrievedValue)
	}
	
	@Test
	@JsName("testSetAndGetCustomObject")
	fun `set and get Custom Object`() = runTest {
		val key = "userKey"
		val user = User(1, "John Doe")
		val serializer = serializer<User>()
		dataStore.set(key, user, serializer)
		val retrievedUser = dataStore.get(key, serializer)
		assertEquals(user, retrievedUser)
	}
	
	@Test
	@JsName("testGetIntOrNull")
	fun `get Int or null`() = runTest {
		val key = "intOrNullKey"
		assertNull(dataStore.getIntOrNull(key))
		
		dataStore.set(key, 100)
		val retrievedValue = dataStore.getIntOrNull(key)
		assertEquals(100, retrievedValue)
	}
	
	@Test
	@JsName("testGetLongOrNull")
	fun `get Long or null`() = runTest {
		val key = "longOrNullKey"
		assertNull(dataStore.getLongOrNull(key))
		
		dataStore.set(key, 987654321L)
		val retrievedValue = dataStore.getLongOrNull(key)
		assertEquals(987654321L, retrievedValue)
	}
	
	@Test
	@JsName("testGetStringOrNull")
	fun `get String or null`() = runTest {
		val key = "stringOrNullKey"
		assertNull(dataStore.getStringOrNull(key))
		
		dataStore.set(key, "Nullable String")
		val retrievedValue = dataStore.getStringOrNull(key)
		assertEquals("Nullable String", retrievedValue)
	}
	
	@Test
	@JsName("testGetBooleanOrNull")
	fun `get Boolean or null`() = runTest {
		val key = "booleanOrNullKey"
		assertNull(dataStore.getBooleanOrNull(key))
		
		dataStore.set(key, true)
		val retrievedValue = dataStore.getBooleanOrNull(key)
		assertTrue(retrievedValue!!)
	}
	
	@Test
	@JsName("testGetFloatOrNull")
	fun `get Float or null`() = runTest {
		val key = "floatOrNullKey"
		assertNull(dataStore.getFloatOrNull(key))
		
		dataStore.set(key, 2.718f)
		val retrievedValue = dataStore.getFloatOrNull(key)
		assertEquals(2.718f, retrievedValue)
	}
	
	@Test
	@JsName("testGetIntFlow")
	fun `get Int Flow`() = runTest {
		val key = "intFlowKey"
		val defaultValue = 10
		val flow = dataStore.getIntFlow(key, defaultValue)
		val initialValue = flow.first()
		assertEquals(defaultValue, initialValue)
		
		dataStore.set(key, 55)
		val updatedValue = flow.first()
		assertEquals(55, updatedValue)
	}
	
	@Test
	@JsName("testGetLongFlow")
	fun `get Long Flow`() = runTest {
		val key = "longFlowKey"
		val defaultValue = 1000L
		val flow = dataStore.getLongFlow(key, defaultValue)
		val initialValue = flow.first()
		assertEquals(defaultValue, initialValue)
		
		dataStore.set(key, 5000L)
		val updatedValue = flow.first()
		assertEquals(5000L, updatedValue)
	}
	
	@Test
	@JsName("testGetStringFlow")
	fun `get String Flow`() = runTest {
		val key = "stringFlowKey"
		val defaultValue = "Default"
		val flow = dataStore.getStringFlow(key, defaultValue)
		val initialValue = flow.first()
		assertEquals(defaultValue, initialValue)
		
		dataStore.set(key, "Updated String")
		val updatedValue = flow.first()
		assertEquals("Updated String", updatedValue)
	}
	
	@Test
	@JsName("testGetBooleanFlow")
	fun `get Boolean Flow`() = runTest {
		val key = "booleanFlowKey"
		val defaultValue = false
		val flow = dataStore.getBooleanFlow(key, defaultValue)
		val initialValue = flow.first()
		assertEquals(defaultValue, initialValue)
		
		dataStore.set(key, true)
		val updatedValue = flow.first()
		assertTrue(updatedValue!!)
	}
	
	@Test
	@JsName("testGetFloatFlow")
	fun `get Float Flow`() = runTest {
		val key = "floatFlowKey"
		val defaultValue = 1.23f
		val flow = dataStore.getFloatFlow(key, defaultValue)
		val initialValue = flow.first()
		assertEquals(defaultValue, initialValue)
		
		dataStore.set(key, 4.56f)
		val updatedValue = flow.first()
		assertEquals(4.56f, updatedValue)
	}
	
	@Test
	@JsName("testGetCustomObjectFlow")
	fun `get Custom Object Flow`() = runTest {
		val key = "userFlowKey"
		val defaultValue: User? = null
		val serializer = serializer<User>()
		val flow = dataStore.getFlow(key, serializer)
		
		val initialValue = flow.first()
		assertNull(initialValue)
		
		val user = User(2, "Jane Smith")
		dataStore.set(key, user, serializer)
		val updatedValue = flow.first()
		assertEquals(user, updatedValue)
	}
	
	@Test
	@JsName("testRemoveKey")
	fun `remove key`() = runTest {
		val key = "removeKey"
		val value = "To be removed"
		dataStore.set(key, value)
		dataStore.remove(key)
		val retrievedValue = dataStore.getStringOrNull(key)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testRemoveNonExistentKey")
	fun `remove non-existent key`() = runTest {
		val key = "nonExistentKey"
		dataStore.remove(key)
		assertNull(dataStore.getStringOrNull(key))
	}
	
	@Test
	@JsName("testClearAll")
	fun `clear all keys`() = runTest {
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
	fun `set null value`() = runTest {
		val key = "nullValueKey"
		dataStore.set<String>(key, null, null)
		val retrievedValue = dataStore.getStringOrNull(key)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testSetNullCustomObject")
	fun `set null Custom Object`() = runTest {
		val key = "nullUserKey"
		val serializer = serializer<User>()
		dataStore.set<User>(key, null, serializer)
		val retrievedUser = dataStore.get<User>(key, serializer)
		assertNull(retrievedUser)
	}
	
	@Test
	@JsName("testGetWithInvalidSerializer")
	fun `get with invalid serializer`() = runTest {
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
	fun `set with invalid serializer`() = runTest {
		val key = "invalidSetSerializerKey"
		val invalidValue = "Invalid Set Serializer"
		val correctSerializer = serializer<User>()
		
		try {
			dataStore.set(key, invalidValue)
		} catch (e: Exception) {
			// Ожидаем исключение из-за попытки сериализовать строку как объект User
		}
		
		val retrievedValue = dataStore.get<User>(key, correctSerializer)
		assertNull(retrievedValue)
	}
	
	@Test
	@JsName("testGetWithDefaultValues")
	fun `get with default values`() = runTest {
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
	fun `get non-existent keys`() = runTest {
		assertNull(dataStore.getIntOrNull("nonExistentInt"))
		assertNull(dataStore.getLongOrNull("nonExistentLong"))
		assertNull(dataStore.getStringOrNull("nonExistentString"))
		assertNull(dataStore.getBooleanOrNull("nonExistentBoolean"))
		assertNull(dataStore.getFloatOrNull("nonExistentFloat"))
	}
	
	@Test
	@JsName("testFlowWithUnconfinedDispatcher")
	fun `flow with unconfined dispatcher`() = runTest {
		val key = "reactiveKey"
		val initialValue = "Initial"
		val updatedValue = "First Update"
		
		val flow = dataStore.getStringFlow(key, initialValue)
		val collectedValues = mutableListOf<String>()
		
		val job = launch(UnconfinedTestDispatcher(testScheduler)) {
			flow.collect { value ->
				println("Collected value: $value")
				collectedValues.add(value)
			}
		}
		
		dataStore.set(key, initialValue)
		advanceUntilIdle()
		assertEquals(listOf(initialValue), collectedValues, "Initial value not emitted")
		
		dataStore.set(key, updatedValue)
		advanceUntilIdle()
		assertEquals(listOf(initialValue, updatedValue), collectedValues, "Updated value not emitted")
		
		dataStore.remove(key)
		advanceUntilIdle()
		println("Collected values: $collectedValues")
		assertEquals(
			listOf(initialValue, updatedValue, initialValue),
			collectedValues.toList(),
			"Default value after key removal not emitted"
		)
		
		job.cancelAndJoin()
	}
}
