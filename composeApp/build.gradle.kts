import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.multiplatform)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.compose)
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlinx.serialization)
}

kotlin {
	jvmToolchain(jdkVersion = JavaVersion.VERSION_17.toString().toInt())
	
	androidTarget()
	
	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		browser()
		binaries.executable()
	}
	
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "ComposeApp"
			isStatic = true
		}
	}
	
	sourceSets {
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.client.serialization)
			implementation(libs.ktor.client.logging)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.androidx.lifecycle.runtime.compose)
			implementation(libs.androidx.navigation.composee)
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

android {
	namespace = "com.courses"
	compileSdk = 35
	
	defaultConfig {
		minSdk = 21
		targetSdk = 35
		
		applicationId = "com.courses.androidApp"
		versionCode = 1
		versionName = "1.0.0"
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}
