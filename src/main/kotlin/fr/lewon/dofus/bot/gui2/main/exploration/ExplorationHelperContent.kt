package fr.lewon.dofus.bot.gui2.main.exploration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import fr.lewon.dofus.bot.gui2.custom.SubTitleText

@Composable
fun ExplorationHelperContent() {
    Box(Modifier.fillMaxSize()) {
        SubTitleText("Work in progress", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
    }
}