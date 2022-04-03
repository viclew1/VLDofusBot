package fr.lewon.dofus.bot.gui.panes.script.selector

import fr.lewon.dofus.bot.gui.panes.script.GlobalScriptPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

class GlobalScriptSelectorPanel : AbstractScriptSelectorPanel() {

    private var startedCount = 0

    fun addListenedCharacter(character: DofusCharacter) {
        removeListenedCharacter(character)
        ScriptRunner.addListener(character, this)
    }

    fun removeListenedCharacter(character: DofusCharacter) {
        ScriptRunner.removeListener(character, this)
    }

    private fun getSelectedCharacters(): List<DofusCharacter> {
        return GlobalScriptPanel.characterFilterPanel.getSelectedCharacters()
    }

    fun updateSelectedCharacters() {
        startedCount = getSelectedCharacters().count { ScriptRunner.isScriptRunning(it) }
        updateButton(startedCount > 0)
    }

    override fun isRunning(): Boolean {
        return getSelectedCharacters().any { ScriptRunner.isScriptRunning(it) }
    }

    override fun getInitialParameterValue(parameter: DofusBotParameter, script: DofusBotScript): String {
        return parameter.defaultValue
    }

    override fun onParamUpdate(script: DofusBotScript, param: DofusBotParameter) {
        for (character in getSelectedCharacters()) {
            CharacterManager.updateParamValue(character, script, param)
        }
    }

    override fun runScript(script: DofusBotScript) {
        for (character in getSelectedCharacters()) {
            ScriptRunner.runScript(character, script)
        }
    }

    override fun stopScript() {
        for (character in getSelectedCharacters()) {
            ScriptRunner.stopScript(character)
        }
    }

    override fun scriptEnded(character: DofusCharacter): Boolean {
        return character in getSelectedCharacters() && --startedCount <= 0
    }

    override fun scriptStarted(character: DofusCharacter): Boolean {
        return character in getSelectedCharacters() && ++startedCount == 1
    }
}