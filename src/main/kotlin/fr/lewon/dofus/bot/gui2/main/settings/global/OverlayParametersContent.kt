package fr.lewon.dofus.bot.gui2.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui2.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui2.main.settings.SettingsUIState
import fr.lewon.dofus.bot.util.listeners.OverlayInfo

@Composable
fun OverlayParametersContent() {
    Column {
        ConfigSwitchLine(
            "Overlays display",
            "",
            true,
            SettingsUIState.settingsGlobalConfig.value.displayOverlays
        ) { checked ->
            SettingsUIState.updateGlobalConfig { it.displayOverlays = checked }
        }
        for (overlayInfo in OverlayInfo.values()) {
            ConfigSwitchLine(
                overlayInfo.title,
                overlayInfo.description,
                SettingsUIState.settingsGlobalConfig.value.displayOverlays,
                SettingsUIState.settingsGlobalConfig.value.shouldDisplayOverlay(overlayInfo)
            ) { checked -> SettingsUIState.updateGlobalConfig { it.shouldDisplayOverlay[overlayInfo] = checked } }
        }
    }
}