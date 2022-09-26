package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.scriptvalues.CharacterScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.filemanagers.impl.ScriptParamManager

object ScriptParametersUIUtil {

    private val uiStateByParameterByScript = HashMap<DofusBotScriptBuilder, MutableState<ScriptBuilderUIState>>()
    private val globalScriptValues = CharacterScriptValues()

    fun getCurrentScriptParameters(): List<DofusBotParameter> {
        return ScriptTabsUIUtil.getCurrentScriptBuilder().getParameters()
    }

    private fun getScriptBuilderUIState(scriptBuilder: DofusBotScriptBuilder): MutableState<ScriptBuilderUIState> {
        return uiStateByParameterByScript.computeIfAbsent(scriptBuilder) { mutableStateOf(ScriptBuilderUIState(emptyMap())) }
    }

    fun getScriptParameterUIState(
        scriptBuilder: DofusBotScriptBuilder,
        parameter: DofusBotParameter
    ): ScriptParameterUIState {
        val builderUIState = getScriptBuilderUIState(scriptBuilder)
        var uiState = builderUIState.value.scriptParameterUIStateByParameter[parameter]
        if (uiState == null) {
            uiState = ScriptParameterUIState(parameter, false, getParamValue(scriptBuilder, parameter))
            val uiStateByParameter = builderUIState.value.scriptParameterUIStateByParameter.plus(parameter to uiState)
            builderUIState.value = builderUIState.value.copy(scriptParameterUIStateByParameter = uiStateByParameter)
        }
        return uiState
    }

    fun updateParamValue(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter, value: String) {
        when (ScriptTabsUIUtil.getCurrentTab()) {
            ScriptTab.INDIVIDUAL ->
                ScriptParamManager.updateParamValue(getSelectedCharacter(), scriptBuilder, parameter, value)
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
        val builderUIState = getScriptBuilderUIState(scriptBuilder)
        val uiStateByParameter = parameters.associateWith { parameter ->
            ScriptParameterUIState(
                parameter,
                parameter.displayCondition(scriptValues),
                getParamValue(scriptBuilder, parameter)
            )
        }
        builderUIState.value = builderUIState.value.copy(scriptParameterUIStateByParameter = uiStateByParameter)
    }

    private fun getParamValue(scriptBuilder: DofusBotScriptBuilder, parameter: DofusBotParameter): String {
        return getScriptValuesStore().getValues(scriptBuilder).getParamValue(parameter)
    }

    fun getScriptValuesStore(): CharacterScriptValues {
        return when (ScriptTabsUIUtil.getCurrentTab()) {
            ScriptTab.INDIVIDUAL -> ScriptParamManager.getCharacterScriptValues(getSelectedCharacter())
            ScriptTab.GLOBAL -> globalScriptValues
        }
    }

}