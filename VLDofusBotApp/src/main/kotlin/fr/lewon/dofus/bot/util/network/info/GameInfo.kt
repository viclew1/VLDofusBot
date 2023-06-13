package fr.lewon.dofus.bot.util.network.info

import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.d2p.maps.cell.MapData
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.overlay.impl.LOSOverlay
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.game.paddock.PaddockItem
import fr.lewon.dofus.bot.sniffer.store.EventStore
import java.awt.Rectangle
import java.util.concurrent.locks.ReentrantLock

class GameInfo(val character: DofusCharacter) {

    var hp = 0
    var maxHp = 0
    val lock = ReentrantLock()
    val logger = character.executionLogger

    val eventStore = EventStore()
    lateinit var connection: DofusConnection
    var gameBounds = Rectangle()
    var completeBounds = Rectangle()

    var shouldInitBoard = true

    val dofusBoard = DofusBoard()

    val fightBoard = FightBoard(this)
    var isCreatureModeToggled = false

    private val interactiveElementsLock = ReentrantLock()
    var interactiveElements: List<InteractiveElement> = emptyList()
        get() = interactiveElementsLock.executeSyncOperation { field }
        set(value) = interactiveElementsLock.executeSyncOperation { field = value }
    var mapData = MapData(emptyMap(), emptyList(), emptyList())
    val entityIdByNpcId = HashMap<Int, Double>()
    val monsterInfoByEntityId = HashMap<Double, GameRolePlayGroupMonsterInformations>()
    val entityPositionsOnMapByEntityId = HashMap<Double, Int>()
    val mainMonstersByGroupOnMap = HashMap<GameRolePlayGroupMonsterInformations, DofusMonster>()
    val paddockItemByCell = HashMap<Int, PaddockItem>()

    var playerBaseCharacteristics: Map<Int, CharacterCharacteristic> = HashMap()
    var playerId = -1.0
    lateinit var currentMap: DofusMap
    var treasureHunt: TreasureHuntMessage? = null
    var drhellerOnMap = false

    var finishedQuestIds: List<Int> = ArrayList()
    var activeQuestIds: List<Int> = ArrayList()
    var finishedObjectiveIds: List<Int> = ArrayList()
    var activeObjectiveIds: List<Int> = ArrayList()

    var weightMax = -1
    var inventoryWeight = -1
    var currentTradeInfo = TradeInfo()
    var inShop = false

    fun updatePlayerFighter() {
        val fighter = fightBoard.getPlayerFighter()
        if (fighter != null) {
            fighter.baseStatsById.putAll(playerBaseCharacteristics)
            fighter.maxHp = maxHp
            fighter.baseHp = hp
        }
    }

    fun buildCharacterBasicInfo(): DofusCharacterBasicInfo {
        return DofusCharacterBasicInfo(
            character.name,
            character.dofusClassId,
            finishedQuestIds,
            activeQuestIds,
            finishedObjectiveIds,
            activeObjectiveIds
        )
    }

    @Synchronized
    fun updateCellData(mapId: Double) {
        mapData = D2PMapsAdapter.getMapData(mapId)
        dofusBoard.updateCells(mapData.completeCellDataByCellId.values.map { it.cellData })
        LOSOverlay.updateOverlay(this)
    }

}