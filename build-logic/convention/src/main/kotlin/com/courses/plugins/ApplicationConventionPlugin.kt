package com.courses.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.courses.configure.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.util.Locale

class ApplicationConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		plugins.apply(libs.findPlugin("android-application").get().get().pluginId)
		
		extensions.configure<ApplicationExtension>(::configureKotlinAndroidApp)
	}
}

internal fun Project.configureKotlinAndroidApp(
	extension: ApplicationExtension,
) = extension.apply {
	val moduleName = path
		.split(":")
		.drop(2)
		.joinToString(".")
		.split("-").joinToString("") { it ->
			it.replaceFirstChar {
				if (it.isLowerCase()) {
					it.titlecase(Locale.ROOT)
				} else {
					it.toString()
				}
			}
		}
	
	configure<ApplicationExtension> {
		compileSdk = 35
		namespace = if (moduleName.isNotEmpty()) {
			"com.courses.$moduleName"
		} else {
			"com.courses"
		}
		
		defaultConfig {
			applicationId = "com.courses.androidApp"
			versionCode = 1
			versionName = "1.0.0"
			testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
			
			compileSdk = libs.findVersion("projectAndroidCompileSdk").get().requiredVersion.toInt()
			minSdk = libs.findVersion("projectAndroidMinSdk").get().requiredVersion.toInt()
		}
	}
	
	compileOptions {
		val javaVersion =
			JavaVersion.toVersion(libs.findVersion("projectJavaVersion").get().requiredVersion)

		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion
	}
}

internal fun Project.configureKotlinMultiplatform(
	extension: KotlinMultiplatformExtension
) = extension.apply {
	jvmToolchain(libs.findVersion("projectJavaVersion").get().requiredVersion.toInt())
	
	androidTarget()
	
	js {
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
}