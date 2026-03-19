plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val githubOwner = providers.gradleProperty("githubOwner").orElse("YOUR_GITHUB_OWNER")
val releaseRepo = providers.gradleProperty("releaseRepo").orElse("PRJ-2026-dfu-updtate-0017-xaio.github.io")

android {
    namespace = "io.xaio.ota"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.xaio.ota"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        buildConfigField("String", "GITHUB_OWNER", "\"${githubOwner.get()}\"")
        buildConfigField("String", "RELEASE_REPO", "\"${releaseRepo.get()}\"")
        buildConfigField(
            "String",
            "CATALOG_URL",
            "\"https://${githubOwner.get()}.github.io/${releaseRepo.get()}/catalog.json\"",
        )
        buildConfigField(
            "String",
            "RELEASES_URL",
            "\"https://${githubOwner.get()}.github.io/${releaseRepo.get()}/releases.json\"",
        )
        buildConfigField("String", "VERSION_SERVICE_UUID", "\"12345678-1234-1234-1234-123456789abc\"")
        buildConfigField("String", "VERSION_CHAR_UUID", "\"12345678-1234-1234-1234-123456789abd\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("no.nordicsemi.android:dfu:2.10.1")
}
