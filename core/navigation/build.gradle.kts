plugins {
    alias(libs.plugins.prumio.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.navigation3.runtime)
            api(libs.kotlinx.serialization.core)
        }
    }
}
