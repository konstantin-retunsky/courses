plugins {
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.compose) apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlinx.serialization) apply false
	alias(libs.plugins.dev.mokkery) apply false
}
