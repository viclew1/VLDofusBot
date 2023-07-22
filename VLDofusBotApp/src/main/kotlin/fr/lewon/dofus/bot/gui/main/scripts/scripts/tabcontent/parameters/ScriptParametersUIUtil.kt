package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.parameters

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import java.util.concurrent.locks.ReentrantLock

object ScriptParametersUIUtil : ComposeUIUtil() {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(ScriptParametersUiState())

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    fun getParameterValues(scriptBuilder: DofusBotScriptBuilder) = lock.executeSyncOperation {
        val uiStateValue = getUiStateValue()
        val parameterValuesByScript = uiStateValue.parameterValuesByScript
        val parameterValues = uiStateValue.parameterValuesByScript[scriptBuilder]
            ?: ParameterValues()
        uiState.value = uiStateValue.copy(
            parameterValuesByScript = parameterValuesByScript.plus(scriptBuilder to parameterValues)
        )
        parameterValues.deepCopy()
    }

    fun <T> updateParameterValue(
        scriptBuilder: DofusBotScriptBuilder,
        parameter: DofusBotParameter<T>,
        value: T,
    ) = lock.executeSyncOperation {
        val uiStateValue = getUiStateValue()
        val parameterValues = getParameterValues(scriptBuilder)
        parameterValues.updateParamValue(parameter, value)
        uiState.value = uiStateValue.copy(
            parameterValuesByScript = uiStateValue.parameterValuesByScript.plus(
                scriptBuilder to parameterValues
            )
        )
    }
}