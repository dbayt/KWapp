plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kwapp"
    compileSdk = 35

    compileSdk = libs.versions.compileSdk.get().toInt() // Reference from TOML
    defaultConfig {
        minSdk = 24
        targetSdk = libs.versions.compileSdk.get().toInt() // You can reference it here too
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true;
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1") // For viewModel()
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // For coroutines and collectAsState()
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.1") // Replace version as needed



}