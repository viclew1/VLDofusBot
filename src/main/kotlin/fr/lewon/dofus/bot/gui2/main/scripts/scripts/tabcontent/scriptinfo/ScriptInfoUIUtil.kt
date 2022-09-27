package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.FormatUtil

object ScriptInfoUIUtil {

    private val scriptInfoUIStateByCharacter = HashMap<DofusCharacter, MutableState<ScriptInfoUIState>>()

    fun getScriptInfoUIState(character: DofusCharacter): MutableState<ScriptInfoUIState> {
        return scriptInfoUIStateByCharacter.computeIfAbsent(character) { mutableStateOf(ScriptInfoUIState()) }
    }

    fun removeScriptInfoUIState(character: DofusCharacter) {
        scriptInfoUIStateByCharacter.remove(character)
    }

    fun updateState(character: DofusCharacter) {
        val runningScript = CharactersUIUtil.getCharacterUIState(character).value.runningScript
        val suffix = if (runningScript != null) {
            val durationStr = FormatUtil.durationToStr(System.currentTimeMillis() - runningScript.startTime)
            "Running time : $durationStr - ${runningScript.scriptBuilder.name}"
        } else "Ready"
        val text = "${character.pseudo} : $suffix"
        val scriptInfoUIState = getScriptInfoUIState(character)
        scriptInfoUIState.value = scriptInfoUIState.value.copy(
            text = text,
            color = if (runningScript == null) Color.Green else Color.White
        )
    }

}