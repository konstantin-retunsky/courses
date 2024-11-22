package com.courses.configure

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Locale

private val defaultPackageName = "com.courses"


internal fun Project.configureAndroidLibrary(extension: LibraryExtension) {
	extension.apply {
		val moduleName = deriveModuleName()
		
		namespace = "$defaultPackageName${if (moduleName.isNotEmpty()) ".$moduleName" else ""}"
		
		compileSdk = libs.findVersion("projectAndroidCompileSdk").get().requiredVersion.toInt()
		
		defaultConfig {
			minSdk = libs.findVersion("projectAndroidMinSdk").get().requiredVersion.toInt()
		}
		
		configureCompileOptions()
	}
}

internal fun Project.configureAndroidApplication(extension: ApplicationExtension) {
	extension.apply {
		val moduleName = deriveModuleName()
		
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

private fun Project.deriveModuleName(): String {
	val moduleParts = path.split(":").drop(2)
	
	val joinedModuleParts = moduleParts.joinToString(".")
	
	return joinedModuleParts.split("-").joinToString("") { part ->
		part.capitalizeFirstChar()
	}
}

private fun String.capitalizeFirstChar(): String = replaceFirstChar {
	if (it.isLowerCase()) {
		it.titlecase(Locale.ROOT)
	} else {
		it.toString()
	}
}


