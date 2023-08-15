package fr.lewon.dofus.bot.gui.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.main.settings.ConfigLine
import fr.lewon.dofus.bot.gui.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui.main.settings.SettingsUIUtil

@Composable
fun GeneralParametersContent() {
    val globalConfig = SettingsUIUtil.SETTINGS_UI_STATE.value.globalConfig
    Column {
        ConfigLine("General", "", true) {}
        ConfigSwitchLine(
            "Stop any script on archmonster found",
            "When your character enters a map with an archmonster on it, its running script will automatically be stopped",
            true,
            globalConfig.stopAnyScriptOnArchmonsterFound
        ) { checked ->
            SettingsUIUtil.updateGlobalConfig { it.stopAnyScriptOnArchmonsterFound = checked }
        }
    }
}