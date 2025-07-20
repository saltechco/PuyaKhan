plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlinAndroid)
	alias(libs.plugins.kotlinCompose)
	alias(libs.plugins.googleProtobuf)
}

android {
	signingConfigs {
		getByName("debug") {
			storeFile = file("D:\\AndroidStudioProjects\\PuyaKhan\\AppKey.jks")
			storePassword = "SalTech#1402"
			keyAlias = "PuyaKhan"
			keyPassword = "SalTech#1402"
		}
		create("release") {
			storeFile = file("D:\\AndroidStudioProjects\\PuyaKhan\\AppKey.jks")
			storePassword = "SalTech#1402"
			keyAlias = "PuyaKhan"
			keyPassword = "SalTech#1402"
		}
	}
	namespace = "ir.saltech.puyakhan"
	compileSdk = 36

	defaultConfig {
		applicationId = "ir.saltech.puyakhan"
		minSdk = 23
		targetSdk = 36
		versionCode = 100454
		versionName = "1.6.25"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
		signingConfig = signingConfigs.getByName("release")
		multiDexEnabled = true
	}

	buildTypes {
		release {
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("release")
		}
		getByName("debug") {
			signingConfig = signingConfigs.getByName("debug")
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
	implementation(libs.core.ktx)
	implementation(libs.lifecycle.runtime.ktx)
	implementation(libs.lifecycle.viewmodel.compose)
	implementation(libs.activity.compose)
	implementation(platform(libs.compose.bom))
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.ui.tooling.preview)
	implementation(libs.material3)
	implementation(libs.androidx.cardview)
	implementation(libs.androidx.recyclerview)
	implementation(libs.androidx.security.crypto)
	implementation(libs.datastore)
	implementation(libs.datastore.preferences)
	implementation(libs.google.gson)
	implementation(libs.google.protobuf)
	implementation(platform(libs.compose.bom))
	testImplementation(libs.junit)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(platform(libs.compose.bom))
	androidTestImplementation(libs.ui.test.junit4)
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