package com.courses

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface DataStorage {
	fun <T> get(key: String, serializer: KSerializer<T>): T?
	fun <T> getFlow(key: String, serializer: KSerializer<T>): Flow<T?>
	fun getInt(key: String, defaultValue: Int = 0): Int
	fun getLong(key: String, defaultValue: Long = 0L): Long
	fun getString(key: String, defaultValue: String = ""): String
	fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
	fun getFloat(key: String, defaultValue: Float = 0f): Float
	
	fun getIntOrNull(key: String): Int?
	fun getLongOrNull(key: String): Long?
	fun getStringOrNull(key: String): String?
	fun getBooleanOrNull(key: String): Boolean?
	fun getFloatOrNull(key: String): Float?
	
	fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>
	fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long>
	fun getStringFlow(key: String, defaultValue: String = ""): Flow<String>
	fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>
	fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float>
	fun <T> set(key: String, value: T?, serializer: KSerializer<T>? = null)
	fun remove(key: String)
	suspend fun clearAll()
}