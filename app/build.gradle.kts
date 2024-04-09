plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dictify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dictify"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //splash screen
    implementation ("androidx.core:core-splashscreen:1.0.1")

    // navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    /*implementation("com.google.mlkit:language-id:17.0.5")
    implementation ("com.google.android.gms:play-services-mlkit-language-id:17.0.0")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation ("com.google.mlkit:translate:17.0.2")
    implementation ("com.google.android.gms:play-services-mlkit-image-labeling:16.0.8")
    implementation ("com.google.firebase:firebase-ml-natural-language:26.6.0")*/

    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation ("com.google.mlkit:translate:17.0.2")
    implementation ("com.google.android.gms:play-services-mlkit-image-labeling:16.0.8")
    implementation(platform(libs.firebase.bom))
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}