plugins {
	alias(libs.plugins.convention.plugin.multiplatform.library)
}

kotlin {
	sourceSets {
		commonMain.dependencies {
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.client.logging)
			implementation(libs.ktor.client.serialization)
			implementation(libs.ktor.serialization.kotlinx.json)
		}
		
		androidMain.dependencies {
			implementation(libs.ktor.client.okhttp)
		}
		
		iosMain.dependencies {
			implementation(libs.ktor.client.darwin)
		}
	}
}