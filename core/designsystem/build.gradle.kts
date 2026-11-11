plugins {
    alias(libs.plugins.prumio.kmp.compose)
}

kotlin {
    sourceSets {
        getByName("androidHostTest").dependencies {
            implementation(projects.core.screenshotTesting)
        }
        commonMain.dependencies {
            // `implementation` on purpose: Material 3 must not leak to other modules.
            // Everything else uses Prumio* components and PrumioTheme tokens (ADR-0008).
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.datetime)
        }
    }
}
