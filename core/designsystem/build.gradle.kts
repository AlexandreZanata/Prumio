plugins {
    alias(libs.plugins.prumio.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // `implementation` on purpose: Material 3 must not leak to other modules.
            // Everything else uses Prumio* components and PrumioTheme tokens (ADR-0008).
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material3)
        }
    }
}
