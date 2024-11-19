rootProject.name = "courses"

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

include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")

include(":features:auth")
include(":features:onboarding")


