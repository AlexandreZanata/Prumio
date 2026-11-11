// Root build: declares every plugin once (apply false) so all modules share one classpath,
// and configures repository-wide formatting. Module configuration lives in build-logic.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.spotless)
}

// Merged coverage report of every shared module: ./gradlew koverHtmlReport (build/reports/kover/html).
// A new shared module must be added here (ModuleStructureTest checks it).
dependencies {
    kover(projects.app.shell)
    kover(projects.core.common)
    kover(projects.core.data)
    kover(projects.core.database)
    kover(projects.core.databaseTesting)
    kover(projects.core.designsystem)
    kover(projects.core.domain)
    kover(projects.core.model)
    kover(projects.core.navigation)
    kover(projects.core.notifications)
    kover(projects.core.testing)
    kover(projects.core.ui)
    kover(projects.feature.agenda)
    kover(projects.feature.capture)
    kover(projects.feature.inbox)
    kover(projects.feature.matters)
    kover(projects.feature.search)
    kover(projects.feature.settings)
    kover(projects.feature.tasks)
    kover(projects.feature.today)
}

spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    val composeRules = "io.nlopez.compose.rules:ktlint:${libs.versions.compose.rules.get()}"
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/.gradle/**", "**/.kotlin/**")
        ktlint(ktlintVersion).customRuleSets(listOf(composeRules))
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**", "**/.gradle/**", "**/.kotlin/**")
        ktlint(ktlintVersion)
    }
}
