package fr.lewon.dofus.bot.gui2.main.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersListContent
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.CharacterEditionContent
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptsTabsContent
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LogsCharacterTabContent
import fr.lewon.dofus.bot.gui2.main.scripts.status.StatusBarContent
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun ScriptsContent() {
    Box {
        Row(Modifier.padding(bottom = 30.dp)) {
            Row(Modifier.width(240.dp)) {
                CharactersListContent()
            }
            Column(Modifier.width(500.dp)) {
                ScriptsTabsContent()
            }
            Divider(Modifier.fillMaxHeight().width(1.dp), color = AppColors.backgroundColor)
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = ScriptTabsUIUtil.getCurrentTab() == ScriptTab.INDIVIDUAL,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {
                    CharacterEditionContent()
                }
                Row(Modifier.fillMaxHeight()) {
                    LogsCharacterTabContent()
                }
            }
        }
        Row(Modifier.align(Alignment.BottomCenter)) {
            StatusBarContent()
        }
    }
}