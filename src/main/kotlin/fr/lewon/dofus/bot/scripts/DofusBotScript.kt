package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class DofusBotScript(val name: String, val isDev: Boolean = false) {

    abstract fun getParameters(): List<DofusBotParameter>

    abstract fun getStats(): List<DofusBotScriptStat>

    abstract fun getDescription(): String

    abstract fun execute(logItem: LogItem, gameInfo: GameInfo)

    override fun toString(): String {
        return name
    }
}