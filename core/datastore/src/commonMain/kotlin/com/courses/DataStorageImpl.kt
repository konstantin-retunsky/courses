package com.courses

import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.get

internal class DataStorageImpl(
	private val observableSettings: ObservableSettings,
) : DataStorage {
	@OptIn(ExperimentalSettingsApi::class)
	private val flowSettings = observableSettings.toFlowSettings()
	
	override fun <T> get(
		key: String,
		serializer: KSerializer<T>,
	): T? {
		val serializedValue = observableSettings.getStringOrNull(key)
		return if (serializedValue != null) {
			try {
				Json.decodeFromString(serializer, serializedValue)
			} catch (e: Exception) {
				null
			}
		} else {
			null
		}
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun <T> getFlow(key: String, serializer: KSerializer<T>): Flow<T?> {
		return flowSettings.getStringOrNullFlow(key)
			.map { serializedValue ->
				if (serializedValue != null) {
					try {
						Json.decodeFromString(serializer, serializedValue)
					} catch (e: Exception) {
						null
					}
				} else {
					null
				}
			}
	}
	
	override fun <T> set(
		key: String,
		value: T?,
		serializer: KSerializer<T>?,
	) {
		when {
			value is Int -> observableSettings.putInt(key, value)
			value is Long -> observableSettings.putLong(key, value)
			value is String -> observableSettings.putString(key, value)
			value is Boolean -> observableSettings.putBoolean(key, value)
			value is Float -> observableSettings.putFloat(key, value)
			value != null && serializer != null -> {
				try {
					val serializedValue = Json.encodeToString(serializer, value)
					observableSettings.putString(key, serializedValue)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
			
			else -> observableSettings.remove(key)
		}
	}
	
	override fun getInt(key: String, defaultValue: Int): Int {
		return observableSettings[key, defaultValue]
	}
	
	override fun getLong(key: String, defaultValue: Long): Long {
		return observableSettings[key, defaultValue]
	}
	
	override fun getString(key: String, defaultValue: String): String {
		return observableSettings[key, defaultValue]
	}
	
	override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
		return observableSettings[key, defaultValue]
	}
	
	override fun getFloat(key: String, defaultValue: Float): Float {
		return observableSettings[key, defaultValue]
	}
	
	override fun getIntOrNull(key: String): Int? {
		return observableSettings.getIntOrNull(key)
	}
	
	override fun getLongOrNull(key: String): Long? {
		return observableSettings.getLongOrNull(key)
	}
	
	override fun getStringOrNull(key: String): String? {
		return observableSettings.getStringOrNull(key)
	}
	
	override fun getBooleanOrNull(key: String): Boolean? {
		return observableSettings.getBooleanOrNull(key)
	}
	
	override fun getFloatOrNull(key: String): Float? {
		return observableSettings.getFloatOrNull(key)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
		return flowSettings.getIntFlow(key, defaultValue)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> {
		return flowSettings.getLongFlow(key, defaultValue)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
		return flowSettings.getStringFlow(key, defaultValue)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
		return flowSettings.getBooleanFlow(key, defaultValue)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
		return flowSettings.getFloatFlow(key, defaultValue)
	}
	
	override fun remove(key: String) {
		observableSettings.remove(key)
	}
	
	@OptIn(ExperimentalSettingsApi::class)
	override suspend fun clearAll() {
		observableSettings.clear()
		flowSettings.clear()
	}
}
