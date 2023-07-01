package fr.lewon.dofus.bot.gui.main.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersListContent
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.CharacterEditionContent
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptsTabsContent
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.logs.LogsCharacterTabContent

@Composable
fun ScriptsContent() {
    Row(Modifier.fillMaxSize()) {
        Row(Modifier.width(180.dp)) {
            CharactersListContent()
        }
        Column(Modifier.weight(1f)) {
            ScriptsTabsContent()
        }
        Column(Modifier.fillMaxHeight().width(525.dp)) {
            AnimatedVisibility(
                visible = ScriptTabsUIUtil.getCurrentTab() == ScriptTab.INDIVIDUAL,
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top),
                modifier = Modifier.heightIn(max = 430.dp)
            ) {
                CharacterEditionContent()
            }
            Row(Modifier.fillMaxHeight()) {
                LogsCharacterTabContent()
            }
        }
    }
}