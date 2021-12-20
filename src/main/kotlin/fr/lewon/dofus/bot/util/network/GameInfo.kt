package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.store.EventStore
import java.awt.Rectangle
import java.util.concurrent.locks.ReentrantLock

class GameInfo(val character: DofusCharacter) {

    private val lock = ReentrantLock(true)

    val logger = character.executionLogger
    val eventStore = EventStore()
    lateinit var connection: DofusConnection
    var gameBounds = Rectangle()
    var completeBounds = Rectangle()

    var shouldInitBoard = true
    val dofusBoard = DofusBoard()
    val fightBoard = FightBoard(this)
    var interactiveElements: List<InteractiveElement> = ArrayList()
    var completeCellDataByCellId = HashMap<Int, CompleteCellData>()
    var entityPositionsOnMapByEntityId = HashMap<Double, Int>()

    var playerBaseCharacteristics: Map<Int, CharacterCharacteristic> = HashMap()
    var playerId = -1.0
    var currentMap = DofusMap()
    var treasureHunt: TreasureHuntMessage? = null
    var drhellerOnMap = false

    fun executeThreadedSyncOperation(operation: () -> Unit) {
        try {
            lock.lockInterruptibly()
            val condition = lock.newCondition()
            Thread {
                executeSyncOperation {
                    condition.signal()
                    operation()
                }
            }.start()
            condition.await()
        } finally {
            lock.unlock()
        }
    }

    fun <T> executeSyncOperation(operation: () -> T): T {
        try {
            lock.lockInterruptibly()
            return operation()
        } finally {
            lock.unlock()
        }
    }

}