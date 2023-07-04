package fr.lewon.dofus.bot.gui.main.exploration.parameters

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.ParameterInput
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType

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
            for ((parameter, value) in ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByParameter) {
                Row(Modifier.padding(vertical = 5.dp)) {
                    val widthRatio = if (parameter.type == DofusBotParameterType.CHOICE) 0.5f else 0.7f
                    Column(Modifier.fillMaxWidth(widthRatio).align(Alignment.CenterVertically)) {
                        CommonText(parameter.key)
                    }
                    Spacer(Modifier.width(10.dp))
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                    ParameterInput(
                        Modifier,
                        parameter,
                        getParamValue = { value },
                        onParamUpdate = {
                            ExplorationUIUtil.explorerUIState.value = ExplorationUIUtil.explorerUIState.value.copy(
                                explorationParameterValuesByParameter = ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByParameter
                                    .plus(parameter to it)
                            )
                        }
                    )
                }
            }
        }
    }
}