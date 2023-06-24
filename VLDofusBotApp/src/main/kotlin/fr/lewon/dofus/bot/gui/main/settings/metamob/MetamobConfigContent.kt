package fr.lewon.dofus.bot.gui.main.settings.metamob

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.HorizontalSeparator
import fr.lewon.dofus.bot.gui.custom.RefreshButton
import fr.lewon.dofus.bot.gui.main.settings.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.external.metamob.MetamobRequestProcessor
import kotlin.math.max
import kotlin.math.min

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
        HorizontalSeparator()
        ConfigLine(
            "Simultaneous ochers",
            "The amount of ochers you're doing simultaneously (Min : 1, Max: 10). \nSTATUSES WILL ONLY BE UPDATED IF YOU PRESS ARCHMONSTERS STATUS UPDATE BUTTON",
            true
        ) {
            ConfigIntegerField(
                metamobConfig.getSafeSimultaneousOchers().toString(),
                onValueChange = { value ->
                    SettingsUIUtil.updateMetamobConfig {
                        it.simultaneousOchers = min(10, max(1, value.toIntOrNull() ?: 1))
                    }
                })
        }
        ConfigLine(
            "Update archmonsters statuses",
            "Update your Metamob archmonsters statuses. If you have less than the simultaneous ochers amount, it will be set to SEARCHED, if you have more : OFFERED, else : NONE",
            true
        ) {
            Row {
                val refreshing = remember { mutableStateOf(false) }
                Text(
                    if (refreshing.value) "..." else "Update",
                    Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
                Spacer(Modifier.width(5.dp))
                Row(Modifier.align(Alignment.CenterVertically).height(30.dp).padding(end = 10.dp)) {
                    RefreshButton(
                        { MetamobMonstersHelper.updateMonstersStatuses() },
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
        HorizontalSeparator()
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