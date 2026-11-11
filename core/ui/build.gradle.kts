plugins {
    alias(libs.plugins.prumio.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.designsystem)
            api(projects.core.model)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(projects.core.common)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
    }
}

// Shared strings (generic actions such as "Close", labels of model enums) are public on purpose:
// features reuse them instead of redefining the same text (docs/architecture/i18n.md).
compose.resources {
    publicResClass = true
}
