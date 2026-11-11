plugins {
    alias(libs.plugins.prumio.kmp.library)
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.sqldelight.runtime)
            implementation(projects.core.common)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.sqldelight.android.driver)
        }
        getByName("androidHostTest").dependencies {
            implementation(projects.core.databaseTesting)
        }
    }
}

// Schema and migration rules: docs/architecture/database-schema.md.
sqldelight {
    databases {
        create("PrumioDatabase") {
            packageName.set("app.prumio.core.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}
