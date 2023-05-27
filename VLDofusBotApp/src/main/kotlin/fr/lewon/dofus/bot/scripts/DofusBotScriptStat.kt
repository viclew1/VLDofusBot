package fr.lewon.dofus.bot.scripts

class DofusBotScriptStat(val key: String, private val defaultValue: String = "/") {

    var value: String = defaultValue

    fun deepCopy(): DofusBotScriptStat {
        return DofusBotScriptStat(key, defaultValue)
    }
    
}