package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.VldbScriptValuesStore
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

object ScriptParametersUIState {

    private val shouldDisplayByParameterByScript =
        HashMap<DofusBotScriptBuilder, HashMap<DofusBotParameter, MutableState<Boolean>>>()
    private val globalScriptValues = VldbScriptValuesStore()

    fun getCurrentScriptParameters(): List<DofusBotParameter> {
        return ScriptTabsUIUtil.getCurrentScriptBuilder().value.getParameters()
    }

    fun shouldDisplay(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter): MutableState<Boolean> {
        val shouldDisplayByParameter = shouldDisplayByParameterByScript.computeIfAbsent(scriptBuilder) { HashMap() }
        val shouldDisplay = shouldDisplayByParameter.computeIfAbsent(parameter) { mutableStateOf(false) }
        val scriptValues = getScriptValuesStore().getValues(scriptBuilder)
        shouldDisplay.value = parameter.displayCondition(scriptValues)
        return shouldDisplay
    }

    fun updateParamValue(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter, value: String) {
        when (ScriptTabsUIUtil.currentPage.value) {
            ScriptTab.INDIVIDUAL -> {
                CharacterManager.updateParamValue(getSelectedCharacter(), scriptBuilder, parameter, value)
            }
            ScriptTab.GLOBAL ->
                globalScriptValues.getValues(scriptBuilder).updateParamValue(parameter, value)
        }
        updateParameters(scriptBuilder)
    }

    private fun getSelectedCharacter(): DofusCharacter {
        return CharactersUIUtil.getSelectedCharacter()
            ?: error("A character should be selected")
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
        return when (ScriptTabsUIUtil.currentPage.value) {
            ScriptTab.INDIVIDUAL -> getSelectedCharacter().scriptValuesStore
            ScriptTab.GLOBAL -> globalScriptValues
        }
    }

}