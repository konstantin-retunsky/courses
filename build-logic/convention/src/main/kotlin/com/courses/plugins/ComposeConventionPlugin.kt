package com.courses.plugins

import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		plugins.apply(libs.findPlugin("compose-compiler").get().get().pluginId)
		plugins.apply(libs.findPlugin("compose").get().get().pluginId)
		
		val composeDeeps = extensions.getByType<ComposeExtension>().dependencies
		
		extensions.getByType<KotlinMultiplatformExtension>().apply {
			sourceSets.apply {
				commonMain.dependencies {
					implementation(composeDeeps.runtime)
					implementation(composeDeeps.foundation)
					implementation(composeDeeps.material3)
					implementation(composeDeeps.materialIconsExtended)
					implementation(composeDeeps.ui)
					implementation(composeDeeps.components.resources)
					implementation(composeDeeps.components.uiToolingPreview)

					implementation(libs.findLibrary("haze").get())
					implementation(libs.findLibrary("haze-materials").get())
				}
				
				androidMain.dependencies {
					implementation(composeDeeps.uiTooling)
					implementation(libs.findLibrary("androidx-activity-compose").get())
				}
			}
		}
	}
}
