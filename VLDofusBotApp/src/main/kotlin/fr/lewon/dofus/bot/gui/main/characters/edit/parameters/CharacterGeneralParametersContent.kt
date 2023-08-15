package fr.lewon.dofus.bot.gui.main.characters.edit.parameters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.custom.ParameterLine
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.model.characters.HarvestableSetParameter
import fr.lewon.dofus.bot.model.characters.IsFrigost2AvailableParameter
import fr.lewon.dofus.bot.model.characters.IsOtomaiTransportAvailableParameter
import fr.lewon.dofus.bot.model.characters.MinAvailableWeightParameter
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun CharacterGeneralParametersContent(characterUIState: CharacterUIState) {
    CustomStyledColumn("Character general parameters", Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().padding(5.dp)) {
            val scrollState = rememberScrollState()
            Column(Modifier.fillMaxSize().padding(end = 10.dp).verticalScroll(scrollState)) {
                OtomaiTransporterParameterContent(characterUIState)
                Frigost2ZaapParameterContent(characterUIState)
                HarvestableSetParameterContent(characterUIState)
                MinAvailableWeightParameterContent(characterUIState)
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState),
            )
        }
    }
}

@Composable
fun OtomaiTransporterParameterContent(characterUIState: CharacterUIState) {
    ParameterLine(
        modifier = Modifier.padding(bottom = 5.dp),
        parameter = IsOtomaiTransportAvailableParameter,
        parameterValues = ParameterValues().also {
            it.updateParamValue(
                IsOtomaiTransportAvailableParameter,
                characterUIState.parameters.isOtomaiTransportAvailable
            )
        },
        onParamUpdate = {
            CharacterManager.updateCharacter(
                name = characterUIState.name,
                characterParameters = characterUIState.parameters.copy(isOtomaiTransportAvailable = it)
            )
        })
}

@Composable
fun Frigost2ZaapParameterContent(characterUIState: CharacterUIState) {
    ParameterLine(
        modifier = Modifier.padding(bottom = 5.dp),
        parameter = IsFrigost2AvailableParameter,
        parameterValues = ParameterValues().also {
            it.updateParamValue(IsFrigost2AvailableParameter, characterUIState.parameters.isFrigost2Available)
        },
        onParamUpdate = {
            CharacterManager.updateCharacter(
                name = characterUIState.name,
                characterParameters = characterUIState.parameters.copy(isFrigost2Available = it)
            )
        })
}

@Composable
fun HarvestableSetParameterContent(characterUIState: CharacterUIState) {
    ParameterLine(
        modifier = Modifier.padding(bottom = 5.dp),
        parameter = HarvestableSetParameter,
        parameterValues = ParameterValues().also {
            it.updateParamValue(HarvestableSetParameter, characterUIState.parameters.harvestableSet)
        },
        onParamUpdate = {
            CharacterManager.updateCharacter(
                name = characterUIState.name,
                characterParameters = characterUIState.parameters.copy(harvestableSet = it)
            )
        }
    )
}

@Composable
fun MinAvailableWeightParameterContent(characterUIState: CharacterUIState) {
    ParameterLine(
        modifier = Modifier.padding(bottom = 5.dp),
        parameter = MinAvailableWeightParameter,
        parameterValues = ParameterValues().also {
            it.updateParamValue(MinAvailableWeightParameter, characterUIState.parameters.minAvailableWeight)
        },
        onParamUpdate = {
            CharacterManager.updateCharacter(
                name = characterUIState.name,
                characterParameters = characterUIState.parameters.copy(minAvailableWeight = it)
            )
        }
    )
}