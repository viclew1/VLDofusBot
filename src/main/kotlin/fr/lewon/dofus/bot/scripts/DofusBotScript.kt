package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.util.logs.LogItem

abstract class DofusBotScript(val name: String) {

    abstract fun getParameters(): List<DofusBotScriptParameter>

    abstract fun getStats(): List<Pair<String, String>>

    abstract fun getDescription(): String

    abstract fun execute(logItem: LogItem? = null)

    override fun toString(): String {
        return name
    }
}