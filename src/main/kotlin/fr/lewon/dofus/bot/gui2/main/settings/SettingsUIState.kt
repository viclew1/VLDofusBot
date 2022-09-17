package fr.lewon.dofus.bot.gui2.main.settings

import fr.lewon.dofus.bot.model.config.GlobalConfig
import fr.lewon.dofus.bot.model.config.MetamobConfig
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager

data class SettingsUIState(
    val globalConfig: GlobalConfig = GlobalConfigManager.readConfig(),
    val metamobConfig: MetamobConfig = MetamobConfigManager.readConfig()
)