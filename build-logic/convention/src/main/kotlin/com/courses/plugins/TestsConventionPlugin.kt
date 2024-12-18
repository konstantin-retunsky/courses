package com.courses.plugins

import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

class TestsConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		tasks.withType<Test> {
			useJUnitPlatform()
		}
		with(pluginManager) {
			apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
			apply(libs.findPlugin("android-library").get().get().pluginId)
			apply(libs.findPlugin("dev-mokkery").get().get().pluginId)
		}
		
		extensions.configure<KotlinMultiplatformExtension> {
			sourceSets.apply {
				commonTest.dependencies {
					implementation(libs.findLibrary("kotlin-test").get())
					implementation(libs.findLibrary("kotlinx-coroutines-test").get())
					implementation(libs.findLibrary("app-cash-turbine").get())
				}
			}
		}
	}
}
