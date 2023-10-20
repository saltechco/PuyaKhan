plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlinAndroid)
}

android {
	signingConfigs {
		create("release") {
			storeFile = file("D:\\AndroidStudioProjects\\PuyaKhan\\AppKey.jks")
			storePassword = "SalTech#1402"
			keyAlias = "PuyaKhan"
			keyPassword = "SalTech#1402"
		}
	}
	namespace = "ir.saltech.puyakhan"
	compileSdk = 34
	buildToolsVersion = "34.0.0"

	defaultConfig {
		applicationId = "ir.saltech.puyakhan"
		minSdk = 21
		targetSdk = 34
		versionCode = 159114
		versionName = "1.5.9.114"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
		signingConfig = signingConfigs.getByName("release")
		multiDexEnabled = true
	}

	buildTypes {
		release {
			isMinifyEnabled = true
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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.3"
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
	implementation(libs.runtime.livedata)
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.ui.tooling.preview)
	implementation(libs.material3)
	implementation(libs.androidx.cardview)
	implementation(libs.androidx.recyclerview)
	implementation(libs.datastore.preferences)
	implementation(platform(libs.compose.bom))
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(platform(libs.compose.bom))
	androidTestImplementation(libs.ui.test.junit4)
	androidTestImplementation(platform(libs.compose.bom))
	debugImplementation(libs.ui.tooling)
	debugImplementation(libs.ui.test.manifest)
}