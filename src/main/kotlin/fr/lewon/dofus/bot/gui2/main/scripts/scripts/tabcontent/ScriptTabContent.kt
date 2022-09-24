package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters.ScriptParametersContent
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters.ScriptParametersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector.ScriptSelectorContent

@Composable
fun ScriptTabContent() {
    Column {
        ScriptSelectorContent()
        val parameters = ScriptParametersUIUtil.getCurrentScriptParameters()
        AnimatedVisibility(
            parameters.isNotEmpty(),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            ScriptParametersContent()
        }
    }
}