plugins {
    alias(libs.plugins.prumio.feature)
}

// Shared light/dark/large-font visual gate (ADR-0020).
kotlin.sourceSets.getByName("androidHostTest").dependencies {
    implementation(projects.core.screenshotTesting)
}
