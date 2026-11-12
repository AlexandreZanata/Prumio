plugins {
    alias(libs.plugins.prumio.kmp.compose)
}

// Test support only: consumed exclusively from test source sets (ModuleRules enforces it).
kotlin {
    sourceSets {
        androidMain.dependencies {
            api(projects.core.designsystem)
            api(libs.androidx.compose.ui.test.junit4)
            api(libs.androidx.compose.ui.test.manifest)
            api(libs.junit4)
            api(libs.robolectric)
            api(libs.roborazzi)
            api(libs.roborazzi.compose)
        }
    }
}
