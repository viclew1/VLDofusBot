package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.model.characters.DofusCharacter

interface ScriptRunnerListener {

    fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType)

    fun onScriptStart(character: DofusCharacter, script: ScriptRunner.RunningScript)

}
