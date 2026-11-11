package app.prumio.tooling.architecturetests

import kotlin.test.Test
import kotlin.test.assertTrue

/** Privacy rules that can be checked statically (PRIVACY.md, docs/domain/business-rules.md#privacy). */
class PrivacyTest {
    @Test
    fun `BR-PRIV-001 the app manifest removes the INTERNET permission`() {
        val manifest = Repository.read("app/android/src/main/AndroidManifest.xml")
        val removal = Regex("""android:name="android\.permission\.INTERNET"\s+tools:node="remove"""")
        assertTrue(removal.containsMatchIn(manifest), "AndroidManifest.xml must remove android.permission.INTERNET")
    }

    @Test
    fun `BR-PRIV-002 no tracking, crash reporting, ads or account SDKs are declared`() {
        val denylist = listOf(
            "firebase",
            "crashlytics",
            "sentry",
            "bugsnag",
            "appcenter",
            "mixpanel",
            "amplitude",
            "segment",
            "posthog",
            "datadog",
            "newrelic",
            "flurry",
            "onesignal",
            "play-services-ads",
            "play-services-analytics",
            "play-services-auth",
            "com.google.android.gms",
            "facebook",
        )
        val buildFiles = listOf("gradle/libs.versions.toml") +
            Repository.modules.map { "$it/build.gradle.kts" }.filter { Repository.exists(it) }
        val violations = buildFiles.flatMap { path ->
            val text = Repository.read(path).lowercase()
            denylist.filter { text.contains(it) }.map { "$path mentions '$it'" }
        }
        assertNoViolations("Prumio has no tracking or account SDKs", violations)
    }
}
