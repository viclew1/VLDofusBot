package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

interface ScriptRunnerListener {

    fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType)

    fun onScriptStart(character: DofusCharacter, script: DofusBotScriptBuilder)

}
