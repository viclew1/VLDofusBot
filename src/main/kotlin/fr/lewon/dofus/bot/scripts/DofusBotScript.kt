package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.core.logs.LogItem

abstract class DofusBotScript(val name: String) {

    abstract fun getParameters(): List<DofusBotScriptParameter>

    abstract fun getStats(): List<DofusBotScriptStat>

    abstract fun getDescription(): String

    abstract fun execute(logItem: LogItem? = null)

    override fun toString(): String {
        return name
    }
}