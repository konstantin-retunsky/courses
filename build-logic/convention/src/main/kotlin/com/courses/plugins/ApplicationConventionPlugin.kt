package com.courses.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.courses.configure.*

class ApplicationConventionPlugin : Plugin<Project> {
	override fun apply(target: Project): Unit = with(target) {
		plugins.apply(libs.findPlugin("android-application").get().get().pluginId)
		plugins.apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
		
		extensions.configure<ApplicationExtension>(::configureAndroidApplication)
		extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
	}
}
