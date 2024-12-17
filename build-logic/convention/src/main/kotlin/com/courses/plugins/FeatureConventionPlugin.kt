package com.courses.plugins

import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeatureConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		plugins.apply(libs.findPlugin("convention-plugin-multiplatform-library").get().get().pluginId)
		plugins.apply(libs.findPlugin("convention-plugin-koin").get().get().pluginId)
		plugins.apply(libs.findPlugin("convention-plugin-compose").get().get().pluginId)
		
		extensions.getByType<KotlinMultiplatformExtension>().apply {
			sourceSets.apply {
				commonMain.dependencies {
					implementation(project(":core:designsystem"))
					
					implementation(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
					implementation(libs.findLibrary("androidx-navigation-compose").get())
				}
				
				androidMain.dependencies {
					implementation(libs.findLibrary("androidx-activity-compose").get())
				}
			}
		}
	}
}
