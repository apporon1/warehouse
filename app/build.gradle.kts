plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Добавлено
}

android {
    namespace = "com.example.warehouse"
    compileSdk = 34 // Изменено с 35 на 34

    defaultConfig {
        applicationId = "com.example.warehouse"
        minSdk = 29
        targetSdk = 34 // Изменено с 35 на 34
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
        sourceCompatibility = JavaVersion.VERSION_17 // Обновлено
        targetCompatibility = JavaVersion.VERSION_17 // Обновлено
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.activity)
    implementation(libs.zxing) // Изменено
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}