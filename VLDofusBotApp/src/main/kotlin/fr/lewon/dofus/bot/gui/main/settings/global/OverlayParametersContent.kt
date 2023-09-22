package fr.lewon.dofus.bot.gui.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui.main.settings.SettingsUIUtil
import fr.lewon.dofus.bot.util.listeners.OverlayInfo

@Composable
fun OverlayParametersContent() {
    val globalConfig = SettingsUIUtil.SETTINGS_UI_STATE.value.globalConfig
    Column {
        ConfigSwitchLine(
            "Overlays display",
            "",
            true,
            globalConfig.displayOverlays
        ) { checked ->
            SettingsUIUtil.updateGlobalConfig { it.displayOverlays = checked }
        }
        for (overlayInfo in OverlayInfo.entries) {
            ConfigSwitchLine(
                overlayInfo.title,
                overlayInfo.description,
                globalConfig.displayOverlays,
                globalConfig.shouldDisplayOverlay(overlayInfo)
            ) { checked -> SettingsUIUtil.updateGlobalConfig { it.shouldDisplayOverlay[overlayInfo] = checked } }
        }
    }
}