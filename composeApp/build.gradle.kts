import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.compose)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.convention.plugin.application)
	alias(libs.plugins.convention.plugin.koin)
}

koinConventionConfig {
	includeCompose = true
}

kotlin {
	sourceSets {
		commonMain.dependencies {
			implementation(projects.core.network)
			implementation(projects.core.datastore)
			
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.runtime.compose)
			implementation(libs.androidx.navigation.composee)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.coil)
			implementation(libs.multiplatform.settings)
			implementation(libs.kotlinx.datetime)
		}
		
		commonTest.dependencies {
			implementation(kotlin("test"))
			@OptIn(ExperimentalComposeLibrary::class)
			implementation(compose.uiTest)
			implementation(libs.kotlinx.coroutines.test)
		}
		
		androidMain.dependencies {
			implementation(compose.uiTooling)
			implementation(libs.androidx.activity.compose)
			implementation(libs.kotlinx.coroutines.android)
		}
		
		iosMain.dependencies {
		}
	}
}
