package app.prumio.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/** Privacy gate: fails when the merged Android manifest requests a network permission. */
@CacheableTask
abstract class VerifyNoNetworkPermissionTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun verify() {
        val manifest = mergedManifest.get().asFile.readText()
        val found = FORBIDDEN_PERMISSIONS.filter { manifest.contains("\"$it\"") }
        if (found.isNotEmpty()) {
            throw GradleException(
                "Privacy gate failed: the merged manifest requests $found. Prumio must never access the network. " +
                    "Remove the permission (tools:node=\"remove\") or the dependency that adds it. See PRIVACY.md.",
            )
        }
        report.get().asFile.writeText("OK: merged manifest requests no network permission.\n")
    }

    private companion object {
        val FORBIDDEN_PERMISSIONS = listOf("android.permission.INTERNET")
    }
}
