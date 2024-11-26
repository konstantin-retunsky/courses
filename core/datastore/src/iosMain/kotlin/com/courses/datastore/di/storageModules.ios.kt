package com.courses.datastore.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
actual fun platformStorageModule(): Module = module {
	single<ObservableSettings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
	single { get<ObservableSettings>().toFlowSettings() }
}