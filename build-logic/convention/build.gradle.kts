plugins {
    `kotlin-dsl`
}

group = "app.prumio.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.prumio.android.application.get().pluginId
            implementationClass = "app.prumio.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("feature") {
            id = libs.plugins.prumio.feature.get().pluginId
            implementationClass = "app.prumio.buildlogic.FeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.prumio.jvm.library.get().pluginId
            implementationClass = "app.prumio.buildlogic.JvmLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = libs.plugins.prumio.kmp.compose.get().pluginId
            implementationClass = "app.prumio.buildlogic.KmpComposeConventionPlugin"
        }
        register("kmpLibrary") {
            id = libs.plugins.prumio.kmp.library.get().pluginId
            implementationClass = "app.prumio.buildlogic.KmpLibraryConventionPlugin"
        }
    }
}
