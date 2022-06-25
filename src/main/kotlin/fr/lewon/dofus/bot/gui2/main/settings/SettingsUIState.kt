package fr.lewon.dofus.bot.gui2.main.settings

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.model.config.GlobalConfig
import fr.lewon.dofus.bot.model.config.MetamobConfig
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager

object SettingsUIState {

    val settingsGlobalConfig = mutableStateOf(GlobalConfigManager.readConfig())
    val settingsMetamobConfig = mutableStateOf(MetamobConfigManager.readConfig())

    fun updateMetamobConfig(update: (MetamobConfig) -> Unit) {
        MetamobConfigManager.editConfig(update)
        settingsMetamobConfig.value = MetamobConfigManager.readConfig()
    }

    fun updateGlobalConfig(update: (GlobalConfig) -> Unit) {
        GlobalConfigManager.editConfig(update)
        settingsGlobalConfig.value = GlobalConfigManager.readConfig()
    }
}