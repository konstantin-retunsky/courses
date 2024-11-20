import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`kotlin-dsl`
}

group = "com.courses.convention"

tasks {
	validatePlugins {
		enableStricterValidation = true
		failOnWarning = true
	}
}

dependencies {
	compileOnly(libs.gradle.plugin.android.gradle)
	compileOnly(libs.gradle.plugin.android.tools.common)
	compileOnly(libs.gradle.plugin.room)
	
	compileOnly(libs.gradle.plugin.kotlin)
	compileOnly(libs.gradle.plugin.compose)
	compileOnly(libs.gradle.plugin.compose.compiler)
}

private val javaVersion = JavaVersion.toVersion(libs.versions.projectJavaVersion.get())

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
	}
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
}

gradlePlugin {
	plugins {
		register("conventionPluginApplication") {
			id = libs.plugins.convention.plugin.application.get().pluginId
			implementationClass = "com.courses.plugins.ApplicationConventionPlugin"
		}
		
		register("conventionPluginLibrary") {
			id = libs.plugins.convention.plugin.library.get().pluginId
			implementationClass = "com.courses.plugins.LibraryConventionPlugin"
		}
	}
}

//TODO("Необходимо поискать линтеры которые будут проверять принципы чистой архитектуры")
//TODO("Необходимо поискать линтеры которые будут проверять принципы чистого кода, чтобы условно флаги булевские не принимала функция")