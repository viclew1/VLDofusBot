package fr.lewon.dofus.bot.gui2.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui2.custom.HorizontalSeparator

@Composable
fun GlobalConfigContent() {
    Column {
        NotificationSoundsParametersContent()
        HorizontalSeparator()
        NetworkInterfaceParametersContent()
        HorizontalSeparator()
        OverlayParametersContent()
    }
}