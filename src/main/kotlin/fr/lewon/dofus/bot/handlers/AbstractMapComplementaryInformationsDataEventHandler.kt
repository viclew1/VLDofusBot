package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.manager.d2o.managers.MonstersManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui.alert.SoundType
import fr.lewon.dofus.bot.gui.overlay.LOSHelper
import fr.lewon.dofus.bot.gui.panes.status.StatusPanel
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameInfo
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
        gameInfo.mainMonstersByGroupOnMap = socketResult.actors
            .filterIsInstance<GameRolePlayGroupMonsterInformations>()
            .associateWith { MonstersManager.getMonster(it.staticInfos.mainCreatureLightInfos.genericId.toDouble()) }
        gameInfo.interactiveElements = socketResult.interactiveElements
        beepIfArchMonsterHere(gameInfo, socketResult.map)
        LOSHelper.updateOverlay(gameInfo)
    }

    private fun beepIfArchMonsterHere(gameInfo: GameInfo, map: DofusMap) {
        gameInfo.mainMonstersByGroupOnMap.entries.firstOrNull { it.value.isMiniBoss }?.let {
            SoundType.RARE_MONSTER_FOUND.playSound()
            val mapStr = "(${map.posX}, ${map.posY})"
            val statusText = "${gameInfo.character.pseudo} found arch monster [${it.value.name}] on map $mapStr"
            StatusPanel.changeText(statusText)
        }
    }

}