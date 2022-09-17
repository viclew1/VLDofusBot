package fr.lewon.dofus.bot.gui2.main.settings.metamob

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui2.main.metamob.util.MetamobRequestProcessor
import fr.lewon.dofus.bot.gui2.main.settings.*

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
        ConfigLine("Metamob pseudo", "Needed to use Metamob, your Metamob pseudo", true) {
            ConfigTextField(
                metamobConfig.metamobPseudo ?: "",
                onValueChange = { value ->
                    SettingsUIUtil.updateMetamobConfig { it.metamobPseudo = value }
                })
        }
        ConfigLine("Test metamob connection", "", true) {
            Row {
                val refreshing = remember { mutableStateOf(false) }
                val connectionStatus = remember { mutableStateOf(ConnectionStatus.NOT_TESTED_YET) }
                if (!refreshing.value && connectionStatus.value == ConnectionStatus.NOT_TESTED_YET) {
                    updateConnectionStatus(refreshing, connectionStatus)
                }
                Text(
                    connectionStatus.value.label,
                    Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp,
                    color = connectionStatus.value.color
                )
                Spacer(Modifier.width(5.dp))
                IconButton(
                    { updateConnectionStatus(refreshing, connectionStatus) },
                    Icons.Default.Refresh,
                    Modifier.align(Alignment.CenterVertically),
                    !refreshing.value,
                )
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

private fun updateConnectionStatus(
    refreshing: MutableState<Boolean>,
    connectionStatus: MutableState<ConnectionStatus>
) {
    connectionStatus.value = ConnectionStatus.PENDING
    refreshing.value = true
    Thread {
        connectionStatus.value = if (MetamobRequestProcessor.checkParameters()) {
            ConnectionStatus.CONNECTION_OK
        } else {
            ConnectionStatus.CONNECTION_KO
        }
        refreshing.value = false
    }.start()
}

private enum class ConnectionStatus(val label: String, val color: Color) {
    PENDING(" ... ", Color.White),
    NOT_TESTED_YET("Not tested yet.", Color.LightGray),
    CONNECTION_OK("Connection OK !", Color.Green),
    CONNECTION_KO("Connection KO.", Color.Red)
}