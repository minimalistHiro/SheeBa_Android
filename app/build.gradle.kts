plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hiroki.sheeba"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hiroki.sheeba"
        minSdk = 29
        targetSdk = 34
        versionCode = 7
        versionName = "1.2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")      // ViewModel
    implementation("io.coil-kt:coil-compose:2.5.0")                         // ImagePicker
    implementation("androidx.activity:activity-ktx:1.6.1")                  // PickVisualMedia
    implementation ("androidx.activity:activity-compose:1.6.1")             // rememberLauncherForActivityResult
    implementation("androidx.navigation:navigation-compose:2.7.6")          // Navigation
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))     // Firebase
    implementation("com.google.firebase:firebase-auth")                     // Firebase Auth
    implementation("com.google.firebase:firebase-firestore-ktx")            // Firebase Firestore
    implementation("com.google.firebase:firebase-storage")                  // Firebase Storage
    implementation("com.google.firebase:firebase-analytics")                // Firebase Analytics
    implementation("com.google.accompanist:accompanist-insets:0.11.0")      // Navigation BottomBar
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.11.0")// Navigation BottomBar
    implementation("com.google.accompanist:accompanist-webview:0.28.0")     // WebView

    // QRCodeScanner
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("androidx.camera:camera-extensions:1.3.1")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-beta02")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
}