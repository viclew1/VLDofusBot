package fr.lewon.dofus.bot.gui.panes.script.selector

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

class CharacterScriptSelectorPanel(private val character: DofusCharacter) : AbstractScriptSelectorPanel() {

    init {
        ScriptRunner.addListener(character, this)
    }

    override fun isRunning(): Boolean {
        return ScriptRunner.isScriptRunning(character)
    }

    override fun getInitialParameterValue(parameter: DofusBotParameter, script: DofusBotScript): String {
        return CharacterManager.getParamValue(character, script, parameter) ?: parameter.defaultValue
    }

    override fun onParamUpdate(script: DofusBotScript, param: DofusBotParameter) {
        CharacterManager.updateParamValue(character, script, param)
    }

    override fun runScript(script: DofusBotScript) {
        ScriptRunner.runScript(character, script)
    }

    override fun stopScript() {
        ScriptRunner.stopScript(character)
    }

    override fun scriptEnded(character: DofusCharacter): Boolean {
        return true
    }

    override fun scriptStarted(character: DofusCharacter): Boolean {
        return true
    }
}