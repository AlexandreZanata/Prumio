plugins {
    alias(libs.plugins.prumio.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.database)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(libs.sqldelight.coroutines)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
        getByName("androidHostTest").dependencies {
            implementation(projects.core.databaseTesting)
        }
    }
}
