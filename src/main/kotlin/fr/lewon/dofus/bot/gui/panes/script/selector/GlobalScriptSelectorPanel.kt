package fr.lewon.dofus.bot.gui.panes.script.selector

import fr.lewon.dofus.bot.gui.panes.script.CharacterFilterPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

class GlobalScriptSelectorPanel(
    private val characterFilterPanel: CharacterFilterPanel
) : AbstractScriptSelectorPanel() {

    private var startedCount = 0

    fun addListenedCharacter(character: DofusCharacter) {
        removeListenedCharacter(character)
        ScriptRunner.addListener(character, this)
    }

    fun removeListenedCharacter(character: DofusCharacter) {
        ScriptRunner.removeListener(character, this)
    }

    fun updateSelectedCharacters() {
        startedCount = characterFilterPanel.getSelectedCharacters().count { ScriptRunner.isScriptRunning(it) }
        updateButton(startedCount > 0)
    }

    override fun isRunning(): Boolean {
        return characterFilterPanel.getSelectedCharacters().any { ScriptRunner.isScriptRunning(it) }
    }

    override fun getInitialParameterValue(parameter: DofusBotParameter, script: DofusBotScript): String {
        return parameter.defaultValue
    }

    override fun onParamUpdate(script: DofusBotScript, param: DofusBotParameter) {
        for (character in characterFilterPanel.getSelectedCharacters()) {
            CharacterManager.updateParamValue(character, script, param)
        }
    }

    override fun runScript(script: DofusBotScript) {
        for (character in characterFilterPanel.getSelectedCharacters()) {
            ScriptRunner.runScript(character, script)
        }
    }

    override fun stopScript() {
        for (character in characterFilterPanel.getSelectedCharacters()) {
            ScriptRunner.stopScript(character)
        }
    }

    override fun scriptEnded(character: DofusCharacter): Boolean {
        return if (character in characterFilterPanel.getSelectedCharacters()) {
            --startedCount <= 0
        } else {
            false
        }
    }

    override fun scriptStarted(character: DofusCharacter): Boolean {
        return if (character in characterFilterPanel.getSelectedCharacters()) {
            ++startedCount == 1
        } else {
            false
        }
    }
}