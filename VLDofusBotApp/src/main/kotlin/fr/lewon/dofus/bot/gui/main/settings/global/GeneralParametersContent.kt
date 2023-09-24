package fr.lewon.dofus.bot.gui.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ComboBox
import fr.lewon.dofus.bot.gui.main.settings.ConfigLine
import fr.lewon.dofus.bot.gui.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui.main.settings.SettingsUIUtil
import fr.lewon.dofus.bot.scripts.tasks.impl.harvest.TransferItemsToBankBehaviour

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
        ConfigLine(
            "Transfer items to bank behaviour",
            "Choose the effect you want to apply when going to the bank to empty your resources inventory",
            true,
        ) {
            ComboBox(
                modifier = Modifier.width(300.dp).height(30.dp),
                selectedItem = globalConfig.transferItemsToBankBehaviour,
                items = TransferItemsToBankBehaviour.entries,
                onItemSelect = {
                    SettingsUIUtil.updateGlobalConfig { config -> config.transferItemsToBankBehaviour = it }
                },
                getItemText = { it.strValue },
            )
        }
    }
}