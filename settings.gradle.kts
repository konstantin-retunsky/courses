rootProject.name = "courses"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	includeBuild("build-logic")
	
	repositories {
		google {
			content {
				includeGroupByRegex("com.android.*")
				includeGroupByRegex("com.google.*")
				includeGroupByRegex("androidx.*")
				includeGroupByRegex("android.*")
			}
		}
		gradlePluginPortal()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositories {
		google {
			content {
				includeGroupByRegex("com.android.*")
				includeGroupByRegex("com.google.*")
				includeGroupByRegex("androidx.*")
				includeGroupByRegex("android.*")
			}
		}
		mavenCentral()
	}
}


include(":composeApp")

include(
	":core:network",
	":core:database",
	":core:datastore",
	":core:designsystem",
)

include(
	":features:auth",
	":features:onboarding",
)


