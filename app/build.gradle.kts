plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" // Version matches Kotlin version
}

android {
    namespace = "com.example.cryptotracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cryptotracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "dagger.hilt.android.testing.HiltTestApplication"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
        viewBinding = true // Good practice, though Compose is primary
    }
    // No kotlinCompilerExtensionVersion here, using the plugin instead
    // composeOptions {
    //    kotlinCompilerExtensionVersion = "..."
    // }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0") // For repeatOnLifecycle

    // Jetpack Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    debugImplementation(platform("androidx.compose:compose-bom:2025.05.00")) // For ui-tooling in debug

    // Jetpack Compose - specific artifacts (versions managed by BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.10.1") // BOM should cover, but explicit for clarity
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0") // BOM should cover

    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-compiler:2.56.2")

    // Retrofit & Moshi - Networking and JSON parsing
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.1") // Moshi codegen for Kotlin

    // Charting Library
    implementation("co.yml:ycharts:2.1.0")

    // Room - Local Database
    implementation("androidx.room:room-runtime:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1") // For Coroutines support

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ViewModel & LiveData (though Flow is preferred with Compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0") // BOM should cover
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0") // BOM should cover - optional if only using Flow

    // Test Implementations
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1") // For testing coroutines
    testImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    kaptTest("com.google.dagger:hilt-compiler:2.56.2")


    // AndroidTest Implementations
    androidTestImplementation("androidx.test.ext:junit:1.3.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // BOM
    debugImplementation("androidx.compose.ui:ui-tooling") // BOM
    debugImplementation("androidx.compose.ui:ui-test-manifest") // BOM
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.56.2")

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
