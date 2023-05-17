package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.parameters.ScriptParametersContent
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.parameters.ScriptParametersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.scriptinfo.CurrentScriptInfoContent
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.selector.ScriptSelectorContent

@Composable
fun ScriptTabContent() {
    Column {
        ScriptSelectorContent()
        val parameters = ScriptParametersUIUtil.getCurrentScriptParameters()
        AnimatedVisibility(
            parameters.isNotEmpty(),
            modifier = Modifier.weight(1f),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            ScriptParametersContent()
        }
        if (parameters.isEmpty()) {
            Spacer(Modifier.weight(1f))
        }
        AnimatedVisibility(
            ScriptTabsUIUtil.isScriptStarted(),
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            CurrentScriptInfoContent()
        }
    }
}