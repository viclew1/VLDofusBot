package fr.lewon.dofus.bot.gui.main.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.gui.main.scripts.parameters.ScriptParametersContent
import fr.lewon.dofus.bot.gui.main.scripts.scriptinfo.CurrentScriptInfoContent
import fr.lewon.dofus.bot.gui.main.scripts.selector.ScriptSelectorContent

@Composable
fun ScriptPanelContent() {
    Column {
        ScriptSelectorContent()
        val parameters = ScriptsUiUtil.getUiStateValue().currentScriptBuilder.getParameters()
        Column(Modifier.fillMaxHeight().weight(1f)) {
            AnimatedVisibility(
                parameters.isNotEmpty(),
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                ScriptParametersContent()
            }
            Spacer(Modifier.fillMaxHeight().weight(1f))
        }
        AnimatedVisibility(
            ScriptsUiUtil.isScriptStarted(),
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            CurrentScriptInfoContent()
        }
    }
}