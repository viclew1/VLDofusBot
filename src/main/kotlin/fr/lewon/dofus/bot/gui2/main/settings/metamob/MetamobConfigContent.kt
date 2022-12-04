package fr.lewon.dofus.bot.gui2.main.settings.metamob

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui2.custom.RefreshButton
import fr.lewon.dofus.bot.gui2.main.settings.ConfigLine
import fr.lewon.dofus.bot.gui2.main.settings.ConfigSwitchLine
import fr.lewon.dofus.bot.gui2.main.settings.ConfigTextField
import fr.lewon.dofus.bot.gui2.main.settings.SettingsUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.external.metamob.MetamobRequestProcessor

@Composable
fun MetamobConfigContent() {
    val metamobConfig = SettingsUIUtil.SETTINGS_UI_STATE.value.metamobConfig
    Column {
        ConfigLine("Metamob unique ID", "Needed to use Metamob, you can find it on Metamob under profile / API", true) {
            ConfigTextField(
                metamobConfig.metamobUniqueID ?: "",
                onValueChange = { value ->
                    SettingsUIUtil.updateMetamobConfig { it.metamobUniqueID = value }
                })
        }
        ConfigLine("Metamob username", "Needed to use Metamob, your Metamob username", true) {
            ConfigTextField(
                metamobConfig.metamobUsername ?: "",
                onValueChange = { value ->
                    SettingsUIUtil.updateMetamobConfig { it.metamobUsername = value }
                })
        }
        ConfigLine("Test metamob connection", "", true) {
            Row {
                val refreshing = remember { mutableStateOf(false) }
                val connectionStatus = remember { mutableStateOf(ConnectionStatus.NOT_TESTED_YET) }
                if (!refreshing.value && connectionStatus.value == ConnectionStatus.NOT_TESTED_YET) {
                    LaunchedEffect(Unit) {
                        Thread {
                            connectionStatus.value = ConnectionStatus.PENDING
                            refreshing.value = true
                            updateConnectionStatus(connectionStatus)
                            refreshing.value = false
                        }.start()
                    }
                }
                Text(
                    connectionStatus.value.label,
                    Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp,
                    color = connectionStatus.value.color
                )
                Spacer(Modifier.width(5.dp))
                Row(Modifier.align(Alignment.CenterVertically).height(30.dp).padding(end = 10.dp)) {
                    RefreshButton(
                        { updateConnectionStatus(connectionStatus) },
                        "",
                        RoundedCornerShape(15),
                        Color.Gray,
                        defaultBackgroundColor = AppColors.VERY_DARK_BG_COLOR,
                        width = 40.dp,
                        refreshing = refreshing
                    )
                }
            }
        }
        ConfigSwitchLine(
            "Update on capture",
            "Auto update metamob when you capture a monster group",
            true,
            metamobConfig.captureAutoUpdate
        ) { checked ->
            SettingsUIUtil.updateMetamobConfig { it.captureAutoUpdate = checked }
        }
        ConfigSwitchLine(
            "Update on trade",
            "Auto update metamob when you obtain or lose soul stones in a trade",
            true,
            metamobConfig.tradeAutoUpdate
        ) { checked ->
            SettingsUIUtil.updateMetamobConfig { it.tradeAutoUpdate = checked }
        }
        ConfigSwitchLine(
            "Update on buy",
            "Auto update metamob when you buy a soul stone in auction house or to a merchant player",
            true,
            metamobConfig.shopAutoUpdate
        ) { checked ->
            SettingsUIUtil.updateMetamobConfig { it.shopAutoUpdate = checked }
        }
    }
}

private fun updateConnectionStatus(connectionStatus: MutableState<ConnectionStatus>) {
    connectionStatus.value = ConnectionStatus.PENDING
    val connectionOk = MetamobMonstersHelper.isMetamobConfigured() && MetamobRequestProcessor.checkParameters()
    connectionStatus.value = if (connectionOk) ConnectionStatus.CONNECTION_OK else ConnectionStatus.CONNECTION_KO
}

private enum class ConnectionStatus(val label: String, val color: Color) {
    PENDING(" ... ", Color.White),
    NOT_TESTED_YET("Not tested yet.", Color.LightGray),
    CONNECTION_OK("Connection OK !", Color.Green),
    CONNECTION_KO("Connection KO.", Color.Red)
}