package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class DofusBotScript(val builder: DofusBotScriptBuilder) {

    private val stats = builder.getStats().map { it.deepCopy() }

    fun getStats(): List<DofusBotScriptStat> {
        return stats.map { it.deepCopy() }
    }

    abstract fun execute(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues)

}