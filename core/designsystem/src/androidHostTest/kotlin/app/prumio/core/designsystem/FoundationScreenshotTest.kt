package app.prumio.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.screenshottesting.ScreenshotTest
import org.junit.Test

class FoundationScreenshotTest : ScreenshotTest() {
    @Test
    fun typography() = screenshot("typography") {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs)) {
            PrumioTextStyle.entries.forEach { PrumioText(text = "${it.name} · Pendências", style = it) }
        }
    }

    @Test
    fun buttons() = screenshot("buttons") {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs)) {
            PrumioButtonVariant.entries.forEach { PrumioButton(text = it.name, onClick = {}, variant = it) }
        }
    }
}
