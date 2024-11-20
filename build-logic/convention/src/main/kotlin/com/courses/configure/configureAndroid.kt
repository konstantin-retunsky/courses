package com.courses.configure

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Locale

internal fun Project.configureAndroid(extension: ApplicationExtension) {
	extension.apply {
		val moduleName = deriveModuleName()
		val defaultPackageName = "com.courses"
		
		compileSdk = libs.findVersion("projectAndroidCompileSdk").get().requiredVersion.toInt()
		namespace = "$defaultPackageName${if (moduleName.isNotEmpty()) ".$moduleName" else ""}"
		
		defaultConfig {
			applicationId = "$defaultPackageName.androidApp"
			versionCode = 1
			versionName = "1.0.0"
			testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
			
			compileSdk = libs.findVersion("projectAndroidCompileSdk").get().requiredVersion.toInt()
			minSdk = libs.findVersion("projectAndroidMinSdk").get().requiredVersion.toInt()
		}
		
		configureCompileOptions()
	}
}

private fun Project.deriveModuleName(): String {
	return path
		.split(":")
		.drop(2)
		.joinToString(".")
		.split("-")
		.joinToString("") {
			it.replaceFirstChar { char ->
				if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
			}
		}
}

private fun Project.configureCompileOptions() {
	extensions.configure<BaseExtension> {
		compileOptions {
			val javaVersion = JavaVersion.toVersion(
				libs.findVersion("projectJavaVersion").get().requiredVersion
			)
			sourceCompatibility = javaVersion
			targetCompatibility = javaVersion
		}
	}
}