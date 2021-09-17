package fr.lewon.dofus.bot.game.info

import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import java.awt.Rectangle

object GameInfo {

    var treasureHunt: TreasureHuntMessage? = null
    var fightBoard = FightBoard()
    var currentMap = DofusMap()
    var inHavenBag = false
    var phorrorOnMap = false
    var bounds = Rectangle()
    var completeBounds = Rectangle()
    var sizeRatio = 1f

}