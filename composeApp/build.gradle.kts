plugins {
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.convention.plugin.application)
	alias(libs.plugins.convention.plugin.koin)
	alias(libs.plugins.convention.plugin.compose)
}

koinConventionConfig {
	includeCompose = true
}

kotlin {
	sourceSets {
		commonMain.dependencies {
			//core
			implementation(projects.core.network)
			implementation(projects.core.datastore)
			
			//features
			implementation(projects.features.auth)
			
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.coil)
			implementation(libs.kotlinx.datetime)
		}
		
		androidMain.dependencies {
			implementation(libs.kotlinx.coroutines.android)
		}
	}
}
