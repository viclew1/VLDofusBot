package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TreasureHuntHintsContent() {
    Row(Modifier.fillMaxSize()) {
        Row(Modifier.width(300.dp)) {
            CurrentHintsStoreContent()
        }
        Column(Modifier.weight(1f)) {
            CharacterHintsContent()
        }
    }
}