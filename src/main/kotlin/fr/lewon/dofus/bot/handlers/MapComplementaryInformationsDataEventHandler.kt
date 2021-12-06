package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.gui.overlay.LOSHelper
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.awt.Toolkit

object MapComplementaryInformationsDataEventHandler : EventHandler<MapComplementaryInformationsDataMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        gameInfo.currentMap = socketResult.map
        gameInfo.drhellerOnMap = socketResult.actors.firstOrNull { it is GameRolePlayTreasureHintInformations } != null
        gameInfo.dofusBoard.updateStartCells(socketResult.fightStartPositions.positionsForChallenger)
        gameInfo.fightBoard.resetFighters()
        gameInfo.entityPositionsOnMapByEntityId.clear()
        socketResult.actors.forEach {
            gameInfo.entityPositionsOnMapByEntityId[it.contextualId] = it.disposition.cellId
        }
        gameInfo.interactiveElements = socketResult.interactiveElements
        beepIfArchMonsterHere(socketResult)
        LOSHelper.updateOverlay(gameInfo.pid)
        VldbLogger.debug("Moved to map [${socketResult.map.posX},${socketResult.map.posY}] ; World : ${socketResult.map.worldMap}")
    }

    private fun beepIfArchMonsterHere(socketResult: MapComplementaryInformationsDataMessage) {
        socketResult.actors
            .filterIsInstance<GameRolePlayGroupMonsterInformations>()
            .map { it.staticInfos.mainCreatureLightInfos }
            .mapNotNull { D2OUtil.getObject("Monsters", it.genericId.toDouble()) }
            .firstOrNull { it["isMiniBoss"]?.toString()?.toBoolean() ?: false }
            ?.let { Toolkit.getDefaultToolkit().beep() }
    }

}