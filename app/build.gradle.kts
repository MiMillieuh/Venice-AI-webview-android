plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "org.amethytstlab.veniceai"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.amethytstlab.veniceai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"
    }
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
}