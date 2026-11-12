plugins {
    alias(libs.plugins.prumio.kmp.library)
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        getByName("commonMain").resources.apply {
            srcDir("src/commonMain/sqldelight")
            include(
                "app/prumio/core/database/reminder.sq",
                "app/prumio/core/database/scheduled_alarm.sq",
                "app/prumio/core/database/snooze.sq",
                "app/prumio/core/database/delivered_alarm.sq",
            )
        }
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
            implementation(libs.robolectric)
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

// Robolectric API 36 uses the JDK 21 FileDescriptor bridge.
tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED")
}
