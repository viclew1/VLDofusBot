package fr.lewon.dofus.bot.gui2.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GlobalConfigContent() {
    Column {
        NotificationSoundsParametersContent()
        Divider(Modifier.fillMaxWidth())
        NetworkInterfaceParametersContent()
        Divider(Modifier.fillMaxWidth())
        OverlayParametersContent()
    }
}