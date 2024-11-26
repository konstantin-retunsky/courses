package com.courses.di

import com.courses.datastore.di.dataStoreModule
import org.koin.core.module.Module

val appModule: List<Module> = listOf(
	dataStoreModule,
	com.courses.network.di.networkModule,
)