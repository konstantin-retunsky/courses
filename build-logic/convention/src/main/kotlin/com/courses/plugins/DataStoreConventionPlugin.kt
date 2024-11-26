package com.courses.plugins

import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

class DataStoreConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		with(pluginManager) {
			apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
			apply(libs.findPlugin("android-library").get().get().pluginId)
		}
		
		extensions.configure<KotlinMultiplatformExtension> {
			sourceSets.apply {
				commonMain.dependencies {
					implementation(libs.findLibrary("multiplatform-settings").get())
					implementation(libs.findLibrary("multiplatform-settings-coroutines").get())
					implementation(libs.findLibrary("multiplatform-settings-serialization").get())
					implementation(libs.findLibrary("multiplatform-settings-no-arg").get())
					implementation(libs.findLibrary("multiplatform-settings-test").get())
					
					implementation(libs.findLibrary("kotlinx-coroutines-core").get())
					implementation(libs.findLibrary("kotlinx-serialization-json").get())
				}
				
				androidMain.dependencies {
					implementation(libs.findLibrary("androidx-datastore").get())
					implementation(libs.findLibrary("androidx-datastore-preferences").get())
					
					implementation(libs.findLibrary("multiplatform-settings-datastore").get())
				}
			}
		}
	}
}
