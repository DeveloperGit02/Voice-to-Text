plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
  // alias(libs.plugins.kotlin.kapt) // Add KAPT from version catalog
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.voicetotype"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.voicetotype"
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
    buildFeatures{
        viewBinding = true
    }



}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.external.antlr)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    //kapt("androidx.room:room-compiler:2.6.1") // Use KAPT for annotation processing

    ksp("androidx.room:room-compiler:2.5.0")


    // Kotlin Coroutines Support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // Optional - Paging support
    implementation("androidx.room:room-paging:2.6.1")



}