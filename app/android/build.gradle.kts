import java.util.Properties

plugins {
    alias(libs.plugins.prumio.android.application)
}

// Secrets stay in the ignored repository-root signing.properties (see signing.properties.example).
val releaseSigningFile = rootProject.file("signing.properties")
val releaseSigning = Properties().apply { if (releaseSigningFile.isFile) releaseSigningFile.inputStream().use(::load) }
val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
if (releaseSigningFile.isFile) {
    require(
        releaseSigningKeys.all {
            !releaseSigning.getProperty(it).isNullOrBlank()
        },
    ) { "Incomplete signing.properties; see signing.properties.example" }
}

android {
    defaultConfig {
        // Permanent once published to a store. See docs/adr/0015-application-id-and-namespace.md.
        applicationId = "app.prumio"
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "app.prumio.app.android.di.ReminderInstrumentation"
    }

    if (releaseSigningFile.isFile) {
        signingConfigs.create("release") {
            storeFile = rootProject.file(releaseSigning.getProperty("storeFile"))
            storePassword = releaseSigning.getProperty("storePassword")
            keyAlias = releaseSigning.getProperty("keyAlias")
            keyPassword = releaseSigning.getProperty("keyPassword")
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            if (releaseSigningFile.isFile) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.app.shell)
    implementation(projects.core.domain)
    implementation(projects.core.notifications)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    androidTestImplementation(projects.core.common)
    androidTestImplementation(projects.core.model)
    debugImplementation(libs.compose.ui.tooling)
}
