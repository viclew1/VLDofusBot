package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.gui.main.status.StatusBarUIUtil
import fr.lewon.dofus.bot.gui.util.SoundType
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.*
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class AbstractMapComplementaryInformationsDataEventHandler<T : MapComplementaryInformationsDataMessage> :
    IEventHandler<T> {

    override fun onEventReceived(socketResult: T, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val map = MapManager.getDofusMap(socketResult.mapId)
        gameInfo.currentMap = map
        gameInfo.drhellerOnMap = socketResult.actors.firstOrNull { it is GameRolePlayTreasureHintInformations } != null
        gameInfo.dofusBoard.updateStartCells(socketResult.fightStartPositions.positionsForChallengers)
        gameInfo.fightBoard.resetFighters()
        gameInfo.entityPositionsOnMapByEntityId.clear()
        gameInfo.entityIdByNpcId.clear()
        gameInfo.monsterInfoByEntityId.clear()
        gameInfo.mainMonstersByGroupOnMap.clear()
        gameInfo.paddockItemByCell.clear()
        socketResult.actors.forEach {
            if (it is GameRolePlayNpcInformations) {
                gameInfo.entityIdByNpcId[it.npcId] = it.contextualId
            } else if (it is GameRolePlayGroupMonsterInformations) {
                gameInfo.monsterInfoByEntityId[it.contextualId] = it
                gameInfo.mainMonstersByGroupOnMap[it] = MonsterManager.getMonster(
                    it.staticInfos.mainCreatureLightInfos.genericId.toDouble()
                )
            }
            gameInfo.entityPositionsOnMapByEntityId[it.contextualId] = it.disposition.cellId
        }
        gameInfo.interactiveElements = socketResult.interactiveElements
        val playerInfo = socketResult.actors.filterIsInstance<GameRolePlayCharacterInformations>()
            .firstOrNull { it.name == gameInfo.character.name }
        if (playerInfo != null) {
            if (gameInfo.shouldInitBoard) {
                gameInfo.shouldInitBoard = false
                gameInfo.playerId = playerInfo.contextualId
                gameInfo.updateCellData(socketResult.mapId)
                CharactersUIUtil.updateState(gameInfo.character)
                println("${gameInfo.character.name} initialized, ID : ${gameInfo.playerId}")
            }
            playerInfo.humanoidInfo.options.filterIsInstance<HumanOptionOrnament>().firstOrNull()?.let { option ->
                CharacterGlobalInformationUIUtil.updateCharacterLevel(gameInfo.character.name, option.level)
            }
            CharactersUIUtil.updateSkin(gameInfo.character, playerInfo.look)
        }
        beepIfSpecialMonsterHere(gameInfo, map)
        CharactersUIUtil.updateMap(gameInfo.character, map)
    }

    private fun beepIfSpecialMonsterHere(gameInfo: GameInfo, map: DofusMap) {
        val archMonster = gameInfo.mainMonstersByGroupOnMap.entries.firstOrNull { it.value.isMiniBoss }?.value
        val questMonster = gameInfo.mainMonstersByGroupOnMap.entries.firstOrNull { it.value.isQuestMonster }?.value
        if (archMonster != null) {
            notifyMonsterSeen(gameInfo, SoundType.ARCH_MONSTER_FOUND, "Arch monster", archMonster, map)
        }
        if (questMonster != null) {
            notifyMonsterSeen(gameInfo, SoundType.QUEST_MONSTER_FOUND, "Quest monster", questMonster, map)
        }
    }

    private fun notifyMonsterSeen(
        gameInfo: GameInfo,
        soundType: SoundType,
        monsterLabel: String,
        monster: DofusMonster,
        map: DofusMap
    ) {
        soundType.playSound()
        val mapStr = "(${map.posX}, ${map.posY}) (ID : ${map.id.toLong()})"
        val statusText = "$monsterLabel [${monster.name}] seen on map $mapStr"
        StatusBarUIUtil.changeText(gameInfo.character, statusText)
    }

}