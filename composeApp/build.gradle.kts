import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.compose)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.convention.plugin.application)
}

kotlin {
	sourceSets {
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.runtime.compose)
			implementation(libs.androidx.navigation.composee)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.client.serialization)
			implementation(libs.ktor.client.logging)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.koin.core)
			implementation(libs.koin.compose)
			implementation(libs.coil)
			implementation(libs.coil.network.ktor)
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
			implementation(libs.ktor.client.okhttp)
		}
		
		iosMain.dependencies {
			implementation(libs.ktor.client.darwin)
		}
	}
}
