package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.*
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType

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
        Column(
            Modifier.fillMaxHeight()
                .fillMaxWidth()
                .padding(5.dp)
                .border(BorderStroke(1.dp, Color.Gray))
                .background(AppColors.DARK_BG_COLOR)
        ) {
            Box(Modifier.fillMaxSize().padding(5.dp)) {
                val state = rememberScrollState()
                Column(Modifier.verticalScroll(state).padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 10.dp)) {
                    ScriptParametersUIUtil.updateParameters(builder)
                    for (parameter in parameters) {
                        if (ScriptParametersUIUtil.getScriptParameterUIState(builder, parameter).displayed) {
                            ParameterLine(builder, parameter)
                            Spacer(Modifier.height(10.dp))
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

@Composable
private fun ParameterLine(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter) {
    Row {
        Column(Modifier.fillMaxWidth(0.5f)) {
            if (parameter.description.isEmpty()) {
                SubTitleText(parameter.key)
            } else {
                CommonText(parameter.key)
                SmallText(parameter.description)
            }
        }
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            Spacer(Modifier.weight(1f))
            ParameterInput(scriptBuilder, parameter)
        }
    }
}

@Composable
private fun ParameterInput(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter) {
    val parameterValue = ScriptParametersUIUtil.getScriptParameterUIState(scriptBuilder, parameter).parameterValue
    val possibleValues = parameter.possibleValues
    when (parameter.type) {
        DofusBotParameterType.BOOLEAN ->
            Switch(
                parameterValue.toBoolean(),
                { ScriptParametersUIUtil.updateParamValue(scriptBuilder, parameter, it.toString()) }
            )
        DofusBotParameterType.CHOICE ->
            ComboBox(
                selectedItem = parameterValue,
                items = possibleValues,
                onItemSelect = { ScriptParametersUIUtil.updateParamValue(scriptBuilder, parameter, it) },
                getItemText = { it },
                maxDropDownHeight = 300.dp
            )
        DofusBotParameterType.INTEGER ->
            SimpleTextField(parameterValue, {
                ScriptParametersUIUtil.updateParamValue(scriptBuilder, parameter, it)
            }, isContentValid = { value ->
                value.isEmpty() || value == "-" || value.toIntOrNull() != null
            }, modifier = Modifier.onFocusChanged {
                if (!it.isFocused && parameterValue.toIntOrNull() == null) {
                    ScriptParametersUIUtil.updateParamValue(scriptBuilder, parameter, "0")
                }
            })
        DofusBotParameterType.STRING ->
            SimpleTextField(parameterValue, { ScriptParametersUIUtil.updateParamValue(scriptBuilder, parameter, it) })
    }
}