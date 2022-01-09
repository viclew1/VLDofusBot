package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.core.manager.i18n.I18NUtil
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
        gameInfo.interactiveElements = socketResult.interactiveElements
        Thread { beepIfArchMonsterHere(gameInfo, socketResult) }.start()
        LOSHelper.updateOverlay(gameInfo)
    }

    private fun beepIfArchMonsterHere(gameInfo: GameInfo, socketResult: MapComplementaryInformationsDataMessage) {
        socketResult.actors
            .filterIsInstance<GameRolePlayGroupMonsterInformations>()
            .map { it.staticInfos.mainCreatureLightInfos }
            .mapNotNull { D2OUtil.getObject("Monsters", it.genericId.toDouble()) }
            .firstOrNull { it["isMiniBoss"]?.toString()?.toBoolean() ?: false }
            ?.let {
                val nameId = it["nameId"].toString().toInt()
                val name = I18NUtil.getLabel(nameId)
                SoundType.RARE_MONSTER_FOUND.playSound()
                val characterName = gameInfo.character.pseudo
                val mapStr = "(${socketResult.map.posX}, ${socketResult.map.posY})"
                val statusText = "$characterName found arch monster [$name] on map $mapStr"
                StatusPanel.changeText(statusText)
            }
    }

}