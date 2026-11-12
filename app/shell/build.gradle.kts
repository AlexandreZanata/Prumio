plugins {
    alias(libs.plugins.prumio.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.designsystem)
            implementation(projects.core.domain)
            implementation(projects.core.navigation)
            implementation(projects.core.notifications)
            implementation(projects.core.ui)

            implementation(projects.feature.agenda)
            implementation(projects.feature.capture)
            implementation(projects.feature.inbox)
            implementation(projects.feature.matters)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.tasks)
            implementation(projects.feature.today)

            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.savedstate)
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.koin.test)
        }
    }
}
