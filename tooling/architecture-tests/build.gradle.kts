plugins {
    alias(libs.plugins.prumio.jvm.library)
}

dependencies {
    testImplementation(libs.junit4)
    testImplementation(libs.konsist)
    testImplementation(libs.kotlin.test.junit)
}

// These tests read the whole repository (sources, build files and docs). Declare it as input so
// Gradle re-runs them whenever anything they check changes, and tell them where the root is.
val repositoryRoot = isolated.rootProject.projectDirectory
tasks.test {
    inputs.files(
        fileTree(repositoryRoot) {
            include("**/*.kt", "**/*.kts", "**/*.md", "**/*.sq", "**/*.xml")
            exclude("**/build/**", "**/.gradle/**", "**/.kotlin/**", ".git/**", ".idea/**")
        },
    ).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("repositoryFiles")
    systemProperty("prumio.repositoryRoot", repositoryRoot.asFile.absolutePath)
}
