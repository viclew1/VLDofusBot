package fr.lewon.dofus.bot.util.network

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import java.awt.Rectangle

class GameInfo(val character: DofusCharacter) {

    var shouldInitBoard = true
    val dofusBoard = DofusBoard()
    val fightBoard = FightBoard(this)
    var snifferId: Long = -1
    var pid: Long = -1
    var interactiveElements: List<InteractiveElement> = ArrayList()
    var completeCellDataByCellId = HashMap<Int, CompleteCellData>()
    var treasureHunt: TreasureHuntMessage? = null
    var entityPositionsOnMapByEntityId = HashMap<Double, Int>()
    var currentMap = DofusMap()
    var playerId = -1.0
    var inHavenBag = false
    var drhellerOnMap = false
    var bounds = Rectangle()

}