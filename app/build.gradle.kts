plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.societybroadcast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.societybroadcast"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //
    androidTestImplementation(libs.androidx.junit.v115)
    implementation (libs.kotlinx.coroutines.play.services)
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.firestore)
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.ui.firestore.v640)
    implementation (libs.facebook.login)
    implementation (libs.facebook.android.sdk)
    implementation (libs.play.services.auth)
    implementation (libs.glide)
    implementation (libs.google.firebase.firestore)
    implementation (libs.firebaseui.firebase.ui.firestore)
    implementation (libs.firebase.ui.auth)
    implementation(libs.firebase.analytics)
}