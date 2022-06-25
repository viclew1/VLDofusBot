package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIState
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CurrentScriptInfoContent() {
    Row(
        Modifier.fillMaxWidth().height(150.dp).padding(5.dp)
            .border(BorderStroke(1.dp, Color.LightGray))
            .background(AppColors.DARK_BG_COLOR)
    ) {
        Column {
            for (character in ScriptTabsUIState.getSelectedCharacters()) {
                ScriptInfoContent(character)
            }
        }
    }
}