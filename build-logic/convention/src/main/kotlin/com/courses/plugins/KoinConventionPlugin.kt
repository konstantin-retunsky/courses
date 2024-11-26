package com.courses.plugins

import com.courses.configure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class KoinConventionExtension {
	var includeCompose: Boolean = false
}

class KoinConventionPlugin : Plugin<Project> {
	override fun apply(project: Project): Unit = with(project) {
		val extension =
			project.extensions.create("koinConventionConfig", KoinConventionExtension::class.java)
		
		project.afterEvaluate {
			project.extensions.getByType<KotlinMultiplatformExtension>().apply {
				sourceSets.apply {
					commonMain.dependencies {
						implementation(libs.findLibrary("koin-core").get())
						
						if (extension.includeCompose) {
							implementation(libs.findLibrary("koin-compose").get())
						}
					}
					
					androidMain.dependencies {
						implementation(libs.findLibrary("koin-android").get())
					}
				}
			}
			
		}
	}
}
