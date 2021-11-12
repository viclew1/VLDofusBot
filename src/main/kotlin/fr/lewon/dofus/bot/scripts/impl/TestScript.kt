package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.util.game.MoveUtil

object TestScript : DofusBotScript("Test") {


    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test"
    }

    override fun execute(logItem: LogItem?) {
        MoveUtil.buildMoveTask(Direction.LEFT).run(logItem)
    }

}