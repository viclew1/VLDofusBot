package fr.lewon.dofus.bot.gui.main.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ComboBox
import fr.lewon.dofus.bot.gui.main.settings.ConfigLine
import fr.lewon.dofus.bot.gui.main.settings.SettingsUIUtil
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

private val networkInterfaces = mutableStateOf(emptyList<String>())

@Composable
fun NetworkInterfaceParametersContent() {
    Column {
        ConfigLine("Network", "", true) {}
        Row {
            ConfigLine("Network interface", "Network interface used to listen to game messages", true) {
                NetworkInterfaceDropDownMenu()
            }
        }
    }
    LaunchedEffect(true) {
        Thread {
            networkInterfaces.value = DofusMessageReceiverUtil.getNetworkInterfaceNames()
        }.start()
    }
}

@Composable
private fun NetworkInterfaceDropDownMenu() {
    val globalConfig = SettingsUIUtil.SETTINGS_UI_STATE.value.globalConfig
    val currentNetworkInterface = globalConfig.networkInterfaceName
    ComboBox(
        Modifier.width(300.dp).height(30.dp),
        currentNetworkInterface,
        networkInterfaces.value,
        { item ->
            Thread {
                SettingsUIUtil.updateGlobalConfig { it.networkInterfaceName = item }
                GameSnifferUtil.updateNetworkInterface()
            }.start()
        },
        { item -> item }
    )
}
