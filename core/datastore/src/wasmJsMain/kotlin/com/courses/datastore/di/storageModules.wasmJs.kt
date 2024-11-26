package com.courses.datastore.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
actual fun platformStorageModule(): Module = module {
	single<ObservableSettings> {
		get<Settings>() as ObservableSettings
	}
	single { get<ObservableSettings>().toFlowSettings() }
}