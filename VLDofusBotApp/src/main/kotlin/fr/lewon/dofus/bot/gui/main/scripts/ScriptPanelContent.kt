package fr.lewon.dofus.bot.gui.main.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import fr.lewon.dofus.bot.gui.main.scripts.parameters.ScriptParametersContent
import fr.lewon.dofus.bot.gui.main.scripts.scriptinfo.CurrentScriptInfoContent
import fr.lewon.dofus.bot.gui.main.scripts.selector.ScriptSelectorContent
import fr.lewon.dofus.bot.gui.main.scripts.selector.ScriptSelectorUIUtil

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScriptPanelContent() {
    Column(Modifier.onPreviewKeyEvent { keyEvent ->
        if (keyEvent.isCtrlPressed && keyEvent.key in listOf(Key.Enter, Key.NumPadEnter)) {
            if (!ScriptsUiUtil.isScriptStarted() && ScriptSelectorUIUtil.uiState.value.isStartButtonEnabled) {
                ScriptsUiUtil.toggleScript()
                true
            } else false
        } else false
    }) {
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