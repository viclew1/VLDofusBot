package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.gui.overlay.LOSHelper
import fr.lewon.dofus.bot.gui.sound.SoundType
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

abstract class AbstractMapComplementaryInformationsDataEventHandler<T : MapComplementaryInformationsDataMessage> :
    EventHandler<T> {

    override fun onEventReceived(socketResult: T, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.currentMap = socketResult.map
        gameInfo.drhellerOnMap = socketResult.actors.firstOrNull { it is GameRolePlayTreasureHintInformations } != null
        gameInfo.dofusBoard.updateStartCells(socketResult.fightStartPositions.positionsForChallenger)
        gameInfo.fightBoard.resetFighters()
        gameInfo.entityPositionsOnMapByEntityId.clear()
        socketResult.actors.forEach {
            gameInfo.entityPositionsOnMapByEntityId[it.contextualId] = it.disposition.cellId
        }
        gameInfo.interactiveElements = socketResult.interactiveElements
        Thread { beepIfArchMonsterHere(socketResult) }.start()
        LOSHelper.updateOverlay(gameInfo)
    }

    private fun beepIfArchMonsterHere(socketResult: MapComplementaryInformationsDataMessage) {
        socketResult.actors
            .filterIsInstance<GameRolePlayGroupMonsterInformations>()
            .map { it.staticInfos.mainCreatureLightInfos }
            .mapNotNull { D2OUtil.getObject("Monsters", it.genericId.toDouble()) }
            .firstOrNull { it["isMiniBoss"]?.toString()?.toBoolean() ?: false }
            ?.let { SoundType.RARE_MONSTER_FOUND.playSound() }
    }

}