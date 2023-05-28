package fr.lewon.dofus.bot.gui.main.devtools

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DevToolsContent() {
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(300.dp).fillMaxHeight()) {
            Row(Modifier.fillMaxHeight().weight(1f)) {
                D2OModuleListContent()
            }
            Row(Modifier.height(200.dp)) {
                I18NLabelReaderContent()
            }
        }
        D2OSelectedModuleContent()
    }
}