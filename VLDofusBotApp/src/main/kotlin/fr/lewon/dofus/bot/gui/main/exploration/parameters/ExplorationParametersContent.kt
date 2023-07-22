package fr.lewon.dofus.bot.gui.main.exploration.parameters

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.ParameterLine
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

@Composable
fun ExplorationParametersContent() {
    Column(Modifier.padding(5.dp).fillMaxWidth().grayBoxStyle()) {
        Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle()) {
            CommonText(
                "Exploration parameters",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(Modifier.padding(5.dp)) {
            val selectedSubAreas = ExplorationUIUtil.mapUIState.value.selectedSubAreaIds.map {
                SubAreaManager.getSubArea(it)
            }
            val parameterValues = ExplorationUIUtil.buildParameterValues(selectedSubAreas)
            for (parameter in ExplorationUIUtil.ExplorerParameters) {
                if (parameter.displayCondition(parameterValues)) {
                    ParameterRow(parameter, parameterValues)
                }
            }
        }
    }
}

@Composable
private fun <T> ParameterRow(parameter: DofusBotParameter<T>, parameterValues: ParameterValues) {
    Row(Modifier.padding(vertical = 5.dp)) {
        ParameterLine(
            parameter = parameter,
            parameterValues = parameterValues,
            showDescription = false,
            onParamUpdate = { newValue ->
                ExplorationUIUtil.explorerUIState.value = ExplorationUIUtil.explorerUIState.value.copy(
                    explorationParameterValues = ExplorationUIUtil.explorerUIState.value.explorationParameterValues.deepCopy().also {
                        it.updateParamValue(parameter, newValue)
                    }
                )
            }
        )
    }
}
