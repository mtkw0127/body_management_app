plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("de.mannodermaus.android-junit5")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.github.triplet.play")
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("../key/release.jks")
            storePassword = "chekera0127"
            keyPassword = "chekera0127"
            keyAlias = "key0"
        }
    }

    compileSdk = 34
    compileSdkPreview = "UpsideDownCakePrivacySandbox"
    defaultConfig {
        applicationId = "com.app.calendar"
        minSdk = 26
        targetSdk = 34
        versionCode = 16
        versionName = "1.17.0"
        applicationId = "com.app.body_manage"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    namespace = "com.app.body_manage"
}

detekt {
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    autoCorrect = true
}

play {
    track.set("internal")
    serviceAccountCredentials.set(file("../key/play-service-key.json"))
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.3")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.4.0-alpha03"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Kotlin components
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // 画像加工
    implementation("com.makeramen:roundedimageview:2.3.0")

    // ロガー
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.5.4")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.6.0-beta03")
    // Material Design
    implementation("androidx.compose.material:material:1.5.4")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.8.2")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("com.google.accompanist:accompanist-pager:0.25.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.24.11-rc")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Junit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.2")
    // 画像
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Text feature
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation("com.google.mlkit:text-recognition-japanese:16.0.0")

    // graph
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Proto DataStore
    implementation("androidx.datastore:datastore-core:1.0.0")

    implementation(platform("com.google.firebase:firebase-bom:30.3.2"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // module-level build.gradle
    implementation("io.github.boguszpawlowski.composecalendar:composecalendar:0.6.0")
}