package com.courses.datastore.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toBlockingObservableSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual fun platformStorageModule() = module {
	single { androidContext().settingsStore }
	single { get<FlowSettings>().toBlockingObservableSettings() }
	singleOf(::DataStoreSettings) { bind<FlowSettings>() }
}

val Context.settingsStore by preferencesDataStore("settings")