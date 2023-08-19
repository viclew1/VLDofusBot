package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle

@Composable
fun TreasureHuntHintsContent() {
    Row(Modifier.fillMaxSize()) {
        Row(Modifier.width(300.dp)) {
            CurrentHintsStoreContent()
        }
        Box(Modifier.fillMaxSize()) {
            Column {
                CharacterHintsContent()
            }
            Row(Modifier.align(Alignment.TopEnd).padding(5.dp).grayBoxStyle()) {
                HintsImportContent()
            }
        }
    }
}