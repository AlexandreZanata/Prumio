plugins {
    alias(libs.plugins.prumio.android.application)
}

android {
    defaultConfig {
        // Permanent once published to a store. See docs/adr/0015-application-id-and-namespace.md.
        applicationId = "app.prumio"
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.app.shell)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    debugImplementation(libs.compose.ui.tooling)
}
