package com.courses.configure

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatform(
	extension: KotlinMultiplatformExtension
) = extension.apply {
	jvmToolchain(libs.findVersion("projectJavaVersion").get().requiredVersion.toInt())
	
	androidTarget()
	
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
}