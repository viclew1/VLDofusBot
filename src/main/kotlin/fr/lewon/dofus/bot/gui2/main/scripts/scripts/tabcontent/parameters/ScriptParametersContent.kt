package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.ParameterLine
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil

@Composable
fun ScriptParametersContent() {
    val builder = ScriptTabsUIUtil.getCurrentScriptBuilder()
    val parameters = ScriptParametersUIUtil.getCurrentScriptParameters()
    Column {
        CommonText(
            "Parameters",
            modifier = Modifier.padding(4.dp),
            fontWeight = FontWeight.SemiBold
        )
        Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
            Box(Modifier.fillMaxSize().padding(5.dp)) {
                val state = rememberScrollState()
                Column(Modifier.verticalScroll(state).padding(end = 10.dp)) {
                    ScriptParametersUIUtil.updateParameters(builder)
                    for (parameter in parameters) {
                        if (ScriptParametersUIUtil.getScriptParameterUIState(builder, parameter).displayed) {
                            Row(Modifier.padding(start = 5.dp, top = 5.dp, bottom = 10.dp)) {
                                ParameterLine(parameter, getParamValue = {
                                    ScriptParametersUIUtil.getScriptParameterUIState(builder, parameter).parameterValue
                                }, onParamUpdate = {
                                    ScriptParametersUIUtil.updateParamValue(builder, parameter, it)
                                }, inputModifier = Modifier.onEnterStartScript())
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state),
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.onEnterStartScript() = this.onPreviewKeyEvent {
    if ((it.key == Key.Enter || it.key == Key.NumPadEnter) && it.type == KeyEventType.KeyDown) {
        if (!ScriptTabsUIUtil.isScriptStarted()) {
            ScriptTabsUIUtil.toggleScript()
        }
        true
    } else {
        false
    }
}
