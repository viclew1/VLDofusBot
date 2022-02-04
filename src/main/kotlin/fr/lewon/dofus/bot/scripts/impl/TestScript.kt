package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.util.network.GameInfo

class TestScript : DofusBotScript("Test") {


    override fun getParameters(): List<DofusBotParameter> {
        return listOf()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test script for development only"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val playerFighter = gameInfo.fightBoard.getPlayerFighter() ?: error("No fighter for player")
        gameInfo.logger.addSubLog("HP : ${playerFighter.getCurrentHp()}", logItem)
        gameInfo.logger.addSubLog("MAX HP : ${playerFighter.maxHp}", logItem)
        val mp = DofusCharacteristics.MOVEMENT_POINTS.getValue(playerFighter)
        gameInfo.logger.addSubLog("MP : $mp", logItem)
        val ap = DofusCharacteristics.ACTION_POINTS.getValue(playerFighter)
        gameInfo.logger.addSubLog("AP : $ap", logItem)
        val range = DofusCharacteristics.RANGE.getValue(playerFighter)
        gameInfo.logger.addSubLog("RANGE : $range", logItem)
    }

}