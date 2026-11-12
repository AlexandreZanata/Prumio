plugins {
    alias(libs.plugins.prumio.kmp.library)
}

// Test support only: consumed exclusively from test source sets (ModuleRules enforces it).
kotlin {
    sourceSets {
        androidMain.dependencies {
            api(projects.core.database)
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}
