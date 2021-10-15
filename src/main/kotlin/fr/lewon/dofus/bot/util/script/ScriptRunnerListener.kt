package fr.lewon.dofus.bot.util.script

import fr.lewon.dofus.bot.scripts.DofusBotScript

interface ScriptRunnerListener {

    fun onScriptEnd(endType: DofusBotScriptEndType)

    fun onScriptStart(script: DofusBotScript)

}
