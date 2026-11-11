# `:core:screenshot-testing`

> Shared base for screenshot tests: renders composables with Robolectric and compares them with
> committed images (Roborazzi), in light, dark and 200 % font.

| | |
|---|---|
| **Layer** | Test support (never shipped: only test source sets may depend on it) |
| **Plugin** | `prumio.kmp.compose` |
| **Package** | `app.prumio.core.screenshottesting` |
| **May depend on** | `:core:common`, `:core:designsystem` |
| **Used by** | `androidHostTest` source sets of `:core:designsystem`, `:core:ui` and features |

## Public API

| Symbol | Contract |
|---|---|
| `ScreenshotTest` | Base class (`@RunWith(RobolectricTestRunner)`, native graphics, SDK 36, 360×640 dp). `screenshot(name) { content }` writes/compares `src/androidHostTest/screenshots/<Class>/<name>_{light,dark,font200}.png` |

## Usage

```kotlin
class PrumioButtonScreenshotTest : ScreenshotTest() {
    @Test
    fun variants() = screenshot("button_variants") { PrumioButton(text = "Save", onClick = {}) }
}
```

- Verify (default, runs in `check`): `./gradlew :core:designsystem:testAndroidHostTest`
- Record after an intended visual change: `./gradlew :core:designsystem:testAndroidHostTest -PrecordScreenshots`,
  then open the changed PNGs and review them before committing. Never re-record to silence an
  unexpected difference.
