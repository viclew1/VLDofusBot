package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.*

@Composable
fun <T> ParameterLine(
    parameter: DofusBotParameter<T>,
    parameterValues: ParameterValues,
    onParamUpdate: (T) -> Unit,
    modifier: Modifier = Modifier,
    inputModifier: Modifier = Modifier,
    showDescription: Boolean = true,
) {
    Row(modifier) {
        Column(Modifier.fillMaxWidth(0.6f).align(Alignment.CenterVertically)) {
            CommonText(parameter.key)
            if (showDescription && parameter.description.isNotBlank()) {
                SmallText(parameter.description)
            }
        }
        Spacer(Modifier.width(10.dp))
        Spacer(Modifier.fillMaxWidth().weight(1f))
        ParameterInput(inputModifier, parameter, parameterValues, onParamUpdate)
    }
}

@Composable
fun <T> ParameterInput(
    modifier: Modifier,
    parameter: DofusBotParameter<T>,
    parameterValues: ParameterValues,
    onParamUpdate: (T) -> Unit,
) {
    val castedOnParamUpdate: (Any?) -> Unit = {
        onParamUpdate(it as T)
    }
    Box(modifier.onTabChangeFocus(LocalFocusManager.current).onFocusHighlight()) {
        when (parameter) {
            is BooleanParameter -> BooleanParameterInput(parameter, parameterValues, castedOnParamUpdate)
            is ChoiceParameter<*> -> ChoiceParameterInput(parameter, parameterValues, castedOnParamUpdate)
            is MultiChoiceParameter<*> -> MultiChoiceParameterInput(parameter, parameterValues, castedOnParamUpdate)
            is IntParameter -> IntParameterInput(parameter, parameterValues, castedOnParamUpdate)
            is LongParameter -> LongParameterInput(parameter, parameterValues, castedOnParamUpdate)
            is StringParameter -> StringParameterInput(parameter, parameterValues, castedOnParamUpdate)
            else -> error("Not supported parameter : ${parameter::class.java}")
        }
    }
}

@Composable
private fun BooleanParameterInput(
    parameter: BooleanParameter,
    parameterValues: ParameterValues,
    onParamUpdate: (Boolean) -> Unit,
) = Switch(
    parameterValues.getParamValue(parameter),
    { onParamUpdate(it) },
    modifier = Modifier.height(20.dp)
)

@Composable
private fun <T> ChoiceParameterInput(
    parameter: ChoiceParameter<T>,
    parameterValues: ParameterValues,
    onParamUpdate: (T) -> Unit,
) {
    ComboBox(
        selectedItem = parameterValues.getParamValue(parameter),
        items = parameter.getAvailableValues(parameterValues),
        onItemSelect = onParamUpdate,
        getItemText = { parameter.valueToString(it) },
        maxDropDownHeight = 300.dp,
    )
}

@Composable
private fun <T> MultiChoiceParameterInput(
    parameter: MultiChoiceParameter<T>,
    parameterValues: ParameterValues,
    onParamUpdate: (T) -> Unit,
) {
    ComboBox(
        selectedItem = parameterValues.getParamValue(parameter).first(),
        items = parameter.getAvailableValues(parameterValues),
        onItemSelect = { onParamUpdate(listOf(it) as T) },
        getItemText = { parameter.valueToString(listOf(it)) },
        maxDropDownHeight = 300.dp,
        multipleChoice = true,
    )
}

@Composable
private fun IntParameterInput(
    parameter: IntParameter,
    parameterValues: ParameterValues,
    onParamUpdate: (Int) -> Unit,
) {
    IntegerTextField(
        parameterValues.getParamValue(parameter).toString(),
        onUpdate = { onParamUpdate(it.toIntOrNull() ?: 0) }
    )
}

@Composable
private fun LongParameterInput(
    parameter: LongParameter,
    parameterValues: ParameterValues,
    onParamUpdate: (Long) -> Unit,
) {
    LongTextField(
        parameterValues.getParamValue(parameter).toString(),
        onUpdate = { onParamUpdate(it.toLongOrNull() ?: 0) }
    )
}

@Composable
private fun StringParameterInput(
    parameter: StringParameter,
    parameterValues: ParameterValues,
    onParamUpdate: (String) -> Unit,
) {
    SimpleTextField(parameterValues.getParamValue(parameter), onParamUpdate)
}