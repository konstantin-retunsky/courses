package com.courses.datastore.di

import com.courses.datastore.DataStore
import com.courses.datastore.DataStoreImpl
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.bind

expect fun platformStorageModule(): Module

val dataStoreModule = module {
	single<Settings> { Settings() }
	singleOf(::DataStoreImpl).bind<DataStore>()
	
	includes(platformStorageModule())
}
