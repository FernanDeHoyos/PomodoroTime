plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.fernan.pomodorotime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fernan.pomodorotime"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3:1.3.2") // o la versión más reciente

    implementation("com.github.AnyChart:AnyChart-Android:1.1.5")
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.compose.foundation:foundation:1.8.3")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.7.1")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    // En tu build.gradle (module)
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    // Annotation processor (KAPT) para generar código Room
    kapt("androidx.room:room-compiler:2.7.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}