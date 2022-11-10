package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.gui2.main.status.StatusBarUIUtil
import fr.lewon.dofus.bot.gui2.util.SoundType
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
        gameInfo.actors = socketResult.actors
        if (gameInfo.shouldInitBoard && !gameInfo.initRequested) {
            gameInfo.initRequested = true
        } else if (!gameInfo.shouldInitBoard) {
            gameInfo.actors.firstOrNull { it.contextualId == gameInfo.playerId }?.let {
                CharactersUIUtil.updateSkin(gameInfo.character, it.look)
                if (it is GameRolePlayCharacterInformations) {
                    it.humanoidInfo.options.filterIsInstance<HumanOptionOrnament>().firstOrNull()?.let { option ->
                        CharacterGlobalInformationUIUtil.updateCharacterLevel(gameInfo.character.name, option.level)
                    }
                }
            }
        }
        beepIfSpecialMonsterHere(gameInfo, map)
        ExplorationUIUtil.exploreMap(map.id)
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