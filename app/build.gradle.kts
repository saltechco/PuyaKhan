plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlinAndroid)
	alias(libs.plugins.kotlinCompose)
  alias(libs.plugins.googleProtobuf)
  alias(libs.plugins.kotlinParcelize)
}

android {
	namespace = "ir.saltech.puyakhan"
	compileSdk = 36

	defaultConfig {
		applicationId = "ir.saltech.puyakhan"
		minSdk = 23
		targetSdk = 36
		versionCode = 101500
		versionName = "1.9.36"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
		multiDexEnabled = true
	}

	buildTypes {
		release {
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
	buildFeatures {
		compose = true
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	implementation(platform(libs.compose.bom))
	implementation(libs.core.ktx)
	implementation(libs.lifecycle.runtime.ktx)
	implementation(libs.lifecycle.viewmodel.compose)
	implementation(libs.activity.compose)
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.material3)
	implementation(libs.androidx.recyclerview)
	implementation(libs.androidx.security.crypto)
	implementation(libs.datastore)
	implementation(libs.datastore.preferences)
	implementation(libs.google.protobuf)
	implementation(libs.ui.tooling.preview)
	testImplementation(libs.junit)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(libs.ui.test.junit4)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(platform(libs.compose.bom))
	debugImplementation(libs.ui.tooling)
	debugImplementation(libs.ui.test.manifest)
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				create("java") {
					option("lite")
				}
			}
		}
	}
}