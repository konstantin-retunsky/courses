package com.courses.di

//expect fun platformStorageModule(): Module
//
//val dataStoreModule = module {
//	single<Settings> { Settings() }
//	singleOf(::DataStorageImpl).bind<DataStorage>()
//	singleOf(::SessionDataStoreRepositoryImpl).bind<SessionDataStoreRepository>()
//
//	includes(platformStorageModule())
//}
