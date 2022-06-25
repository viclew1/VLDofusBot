package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIState
import fr.lewon.dofus.bot.model.characters.VldbScriptValuesStore
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

object ScriptParametersUIState {

    private val shouldDisplayByParameterByScript =
        HashMap<DofusBotScriptBuilder, HashMap<DofusBotParameter, MutableState<Boolean>>>()
    private val globalScriptValues = VldbScriptValuesStore()

    fun getCurrentScriptParameters(): List<DofusBotParameter> {
        return ScriptTabsUIState.getCurrentScriptBuilder().value.getParameters()
    }

    fun shouldDisplay(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter): MutableState<Boolean> {
        val shouldDisplayByParameter = shouldDisplayByParameterByScript.computeIfAbsent(scriptBuilder) { HashMap() }
        val shouldDisplay = shouldDisplayByParameter.computeIfAbsent(parameter) { mutableStateOf(false) }
        val scriptValues = getScriptValuesStore().getValues(scriptBuilder)
        shouldDisplay.value = parameter.displayCondition(scriptValues)
        return shouldDisplay
    }

    fun updateParamValue(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter, value: String) {
        when (ScriptTabsUIState.currentPage.value) {
            ScriptTab.INDIVIDUAL ->
                CharacterManager.updateParamValue(
                    ScriptTabsUIState.getCurrentCharacter(), scriptBuilder, parameter, value
                )
            ScriptTab.GLOBAL ->
                globalScriptValues.getValues(scriptBuilder).updateParamValue(parameter, value)
        }
        updateParameters(scriptBuilder)
    }

    fun updateParameters(scriptBuilder: DofusBotScriptBuilder) {
        val scriptValues = getScriptValuesStore().getValues(scriptBuilder)
        val parameters = scriptBuilder.getParameters()
        val shouldDisplayByParameter = shouldDisplayByParameterByScript.computeIfAbsent(scriptBuilder) { HashMap() }
        parameters.forEach { parameter ->
            shouldDisplayByParameter[parameter]?.value = parameter.displayCondition(scriptValues)
        }
    }

    private fun getParamValue(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter): String {
        return getScriptValuesStore().getValues(scriptBuilder).getParamValue(parameter)
    }

    fun getScriptValuesStore(): VldbScriptValuesStore {
        return when (ScriptTabsUIState.currentPage.value) {
            ScriptTab.INDIVIDUAL -> ScriptTabsUIState.getCurrentCharacter().scriptValuesStore
            ScriptTab.GLOBAL -> globalScriptValues
        }
    }

}