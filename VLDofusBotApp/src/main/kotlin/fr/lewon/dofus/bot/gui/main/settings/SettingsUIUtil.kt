package fr.lewon.dofus.bot.gui.main.settings

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.config.GlobalConfig
import fr.lewon.dofus.bot.model.config.MetamobConfig
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager

object SettingsUIUtil : ComposeUIUtil() {

    val SETTINGS_UI_STATE = mutableStateOf(SettingsUIState())

    fun updateMetamobConfig(update: (MetamobConfig) -> Unit) {
        MetamobConfigManager.editConfig(update)
        SETTINGS_UI_STATE.value = SETTINGS_UI_STATE.value.copy(metamobConfig = MetamobConfigManager.readConfig())
    }

    fun updateGlobalConfig(update: (GlobalConfig) -> Unit) {
        GlobalConfigManager.editConfig(update)
        SETTINGS_UI_STATE.value = SETTINGS_UI_STATE.value.copy(globalConfig = GlobalConfigManager.readConfig())
    }

}