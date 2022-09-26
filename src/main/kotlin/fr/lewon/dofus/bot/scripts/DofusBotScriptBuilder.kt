package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class DofusBotScriptBuilder(val name: String, val isDev: Boolean = false) {

    abstract fun getParameters(): List<DofusBotParameter>

    abstract fun getStats(): List<DofusBotScriptStat>

    abstract fun getDescription(): String

    fun buildScript(): DofusBotScript {
        return object : DofusBotScript(this) {
            override fun execute(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
                doExecuteScript(logItem, gameInfo, scriptValues)
            }
        }
    }

    protected abstract fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues)

}