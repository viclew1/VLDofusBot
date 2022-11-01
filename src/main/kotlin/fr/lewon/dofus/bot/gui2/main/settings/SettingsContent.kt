package fr.lewon.dofus.bot.gui2.main.settings

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.TitleText
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.settings.global.GlobalConfigContent
import fr.lewon.dofus.bot.gui2.main.settings.metamob.MetamobConfigContent

@Composable
fun SettingsContent() {
    Box(Modifier.fillMaxSize().padding(5.dp)) {
        val scrollState = rememberScrollState()
        Column(Modifier.verticalScroll(scrollState).padding(end = 14.dp)) {
            TitleText("Global configuration", Modifier.padding(4.dp))
            Row(Modifier.fillMaxWidth().grayBoxStyle().padding(5.dp)) {
                GlobalConfigContent()
            }
            Spacer(Modifier.height(10.dp))
            TitleText("Metamob configuration", Modifier.padding(4.dp))
            Row(Modifier.fillMaxWidth().grayBoxStyle().padding(5.dp)) {
                MetamobConfigContent()
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}