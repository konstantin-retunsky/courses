plugins {
	alias(libs.plugins.convention.plugin.koin)
	alias(libs.plugins.convention.plugin.tests)
	alias(libs.plugins.convention.plugin.feature)
	alias(libs.plugins.kotlinx.serialization)
}

koinConventionConfig {
	includeCompose = true
}
