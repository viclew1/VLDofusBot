package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType

@Composable
fun ParameterLine(
    parameter: DofusBotParameter,
    getParamValue: (DofusBotParameter) -> String,
    onParamUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
    inputModifier: Modifier = Modifier
) {
    Row(modifier) {
        Column(Modifier.fillMaxWidth(0.5f).align(Alignment.CenterVertically)) {
            CommonText(parameter.key)
            if (parameter.description.isNotBlank()) {
                SmallText(parameter.description)
            }
        }
        Spacer(Modifier.width(10.dp))
        Spacer(Modifier.fillMaxWidth().weight(1f))
        ParameterInput(inputModifier, parameter, getParamValue, onParamUpdate)
    }
}

@Composable
fun ParameterInput(
    modifier: Modifier,
    parameter: DofusBotParameter,
    getParamValue: (DofusBotParameter) -> String,
    onParamUpdate: (String) -> Unit
) {
    val parameterValue = getParamValue(parameter)
    val possibleValues = parameter.possibleValues
    val focusManager = LocalFocusManager.current
    Box(modifier.onTabChangeFocus(focusManager).onFocusHighlight()) {
        when (parameter.type) {
            DofusBotParameterType.BOOLEAN ->
                Switch(
                    parameterValue.toBoolean(),
                    { onParamUpdate(it.toString()) },
                    modifier = Modifier.height(20.dp)
                )
            DofusBotParameterType.CHOICE ->
                ComboBox(
                    selectedItem = parameterValue,
                    items = possibleValues,
                    onItemSelect = onParamUpdate,
                    getItemText = { it },
                    maxDropDownHeight = 300.dp,
                )
            DofusBotParameterType.MULTIPLE_CHOICE ->
                ComboBox(
                    selectedItem = parameterValue,
                    items = possibleValues,
                    onItemSelect = onParamUpdate,
                    getItemText = { it },
                    maxDropDownHeight = 300.dp,
                    multipleChoice = true,
                )
            DofusBotParameterType.INTEGER ->
                IntegerTextField(parameterValue, onParamUpdate)
            DofusBotParameterType.STRING ->
                SimpleTextField(parameterValue, onParamUpdate)
        }
    }
}