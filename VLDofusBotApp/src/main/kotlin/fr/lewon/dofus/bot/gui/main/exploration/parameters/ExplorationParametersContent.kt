package fr.lewon.dofus.bot.gui.main.exploration.parameters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.custom.ParameterLine
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

@Composable
fun ExplorationParametersContent() {
    CustomStyledColumn("Exploration parameters", Modifier.padding(5.dp).fillMaxWidth()) {
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
