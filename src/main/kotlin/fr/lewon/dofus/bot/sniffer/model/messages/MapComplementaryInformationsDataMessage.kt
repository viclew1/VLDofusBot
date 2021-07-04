package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.element.StatedElement
import fr.lewon.dofus.bot.sniffer.model.types.fight.FightCommonInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.FightStartingPositions
import fr.lewon.dofus.bot.sniffer.model.types.house.HouseInformations
import fr.lewon.dofus.bot.sniffer.model.types.obstacles.MapObstacle
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager

open class MapComplementaryInformationsDataMessage : INetworkType {

    lateinit var dofusMap: DofusMap
    var houses = ArrayList<HouseInformations>()
    var actors = ArrayList<GameRolePlayActorInformations>()
    var interactiveElements = ArrayList<InteractiveElement>()
    var statedElements = ArrayList<StatedElement>()
    var obstacles = ArrayList<MapObstacle>()
    var fights = ArrayList<FightCommonInformations>()
    var hasAggressiveMonsters = false
    lateinit var fightStartPositions: FightStartingPositions

    override fun deserialize(stream: ByteArrayReader) {
        val subAreaId = stream.readVarShort()
        val mapId = stream.readDouble()
        dofusMap = DTBDofusMapManager.getDofusMap(mapId)
        for (i in 0 until stream.readShort()) {
            val house = TypeManager.getInstance<HouseInformations>(stream.readShort())
            house.deserialize(stream)
            houses.add(house)
        }
        for (i in 0 until stream.readShort()) {
            val actor = TypeManager.getInstance<GameRolePlayActorInformations>(stream.readShort())
            actor.deserialize(stream)
            actors.add(actor)
        }
        for (i in 0 until stream.readShort()) {
            val interactiveElement = TypeManager.getInstance<InteractiveElement>(stream.readShort())
            interactiveElement.deserialize(stream)
            interactiveElements.add(interactiveElement)
        }
        for (i in 0 until stream.readShort()) {
            val statedElement = StatedElement()
            statedElement.deserialize(stream)
            statedElements.add(statedElement)
        }
        for (i in 0 until stream.readShort()) {
            val obstacle = MapObstacle()
            obstacle.deserialize(stream)
            obstacles.add(obstacle)
        }
        for (i in 0 until stream.readShort()) {
            val fight = FightCommonInformations()
            fight.deserialize(stream)
            fights.add(fight)
        }
        hasAggressiveMonsters = stream.readBoolean()
        fightStartPositions = FightStartingPositions()
        fightStartPositions.deserialize(stream)
    }

    fun isPhorrorHere(): Boolean {
        return actors.firstOrNull { it is GameRolePlayTreasureHintInformations } != null
    }

}