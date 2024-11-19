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
	versionCatalogs {
		create("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}

rootProject.name = "build-logic"
include(":convention")
