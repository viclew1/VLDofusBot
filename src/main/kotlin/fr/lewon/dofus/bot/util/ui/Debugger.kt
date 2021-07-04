package fr.lewon.dofus.bot.util.ui

object Debugger {

    private const val DEBUG_ON = true

    fun debug(str: String) {
        if (DEBUG_ON) println(str)
    }

}