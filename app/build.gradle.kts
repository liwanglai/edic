plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.ochess.edict"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ochess.edict"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        named("main") {
            jniLibs.srcDirs("libs")
            res.srcDirs(
                "src/main/res_skin",
                "src/main/res_EnglishWhiz",
                "src/main/res",
            )
        }
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
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.foundation.android)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.hilt)
    implementation(libs.androidx.room.runtime)
    kapt(libs.google.hilt.compiler)
    kapt(libs.androidx.room.compiler)
    kapt(libs.kotlin.reflect)


    implementation(fileTree(mapOf("dir"  to "libs", "include" to  listOf("*.aar", "*.jar"), "exclude" to  listOf<String>())))
    implementation("androidx.compose.material:material:1.1.0")

    implementation("com.google.accompanist:accompanist-glide:latest.release")
    implementation("com.google.android.material:material:1.4.0")

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.5.2")
    // GSON
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Bugly
    implementation("com.tencent.bugly:crashreport:latest.release")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    implementation(mapOf( "group" to "com.fasterxml.jackson.core", "name" to "jackson-core", "version" to "2.13.3"))
    implementation(mapOf( "group" to "com.fasterxml.jackson.core", "name" to "jackson-databind", "version" to "2.13.3"))
}