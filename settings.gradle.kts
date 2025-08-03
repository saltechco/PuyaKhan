pluginManagement {
	repositories {
		maven {
			url = uri("jitpack.io")
		}
		maven {
			url = uri("https://maven.myket.ir")
		}
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven {
			url = uri("https://maven.myket.ir")
		}
		maven {
			url = uri("jitpack.io")
		}
		google()
	}
}

rootProject.name = "PuyaKhan"
include(":app")

