plugins {
    alias(libs.plugins.prumio.kmp.library)
}

kotlin {
    android { androidResources { enable = true } }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
            implementation(libs.androidx.work.runtime)
        }
        getByName("androidHostTest").dependencies { implementation(libs.robolectric) }
        commonTest.dependencies {
            implementation(projects.core.testing)
        }
    }
}

// Robolectric API 36 uses the JDK 21 FileDescriptor bridge.
tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED")
}
