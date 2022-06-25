package fr.lewon.dofus.bot.gui2.main.settings

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.TitleText
import fr.lewon.dofus.bot.gui2.main.settings.global.GlobalConfigContent
import fr.lewon.dofus.bot.gui2.main.settings.metamob.MetamobConfigContent

@Composable
fun SettingsContent() {
    Box(Modifier.fillMaxSize().padding(10.dp)) {
        val scrollState = rememberScrollState()
        Column(Modifier.verticalScroll(scrollState).padding(end = 14.dp)) {
            TitleText("Global configuration")
            Row(Modifier.fillMaxWidth()) {
                GlobalConfigContent()
            }
            Divider(Modifier.fillMaxWidth().height(2.dp))
            Spacer(Modifier.height(10.dp))
            TitleText("Metamob configuration")
            Row {
                MetamobConfigContent()
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}