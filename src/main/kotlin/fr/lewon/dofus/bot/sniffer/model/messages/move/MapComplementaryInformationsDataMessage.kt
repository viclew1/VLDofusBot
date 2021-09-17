package fr.lewon.dofus.bot.sniffer.model.messages.move

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt.GameRolePlayTreasureHintInformations
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.element.StatedElement
import fr.lewon.dofus.bot.sniffer.model.types.fight.FightCommonInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.FightStartingPositions
import fr.lewon.dofus.bot.sniffer.model.types.house.HouseInformations
import fr.lewon.dofus.bot.sniffer.model.types.obstacles.MapObstacle
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class MapComplementaryInformationsDataMessage : INetworkMessage {

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
        for (i in 0 until stream.readUnsignedShort()) {
            val house = TypeManager.getInstance<HouseInformations>(stream.readUnsignedShort())
            house.deserialize(stream)
            houses.add(house)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            val actor = TypeManager.getInstance<GameRolePlayActorInformations>(stream.readUnsignedShort())
            actor.deserialize(stream)
            actors.add(actor)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            val interactiveElement = TypeManager.getInstance<InteractiveElement>(stream.readUnsignedShort())
            interactiveElement.deserialize(stream)
            interactiveElements.add(interactiveElement)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            val statedElement = StatedElement()
            statedElement.deserialize(stream)
            statedElements.add(statedElement)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            val obstacle = MapObstacle()
            obstacle.deserialize(stream)
            obstacles.add(obstacle)
        }
        for (i in 0 until stream.readUnsignedShort()) {
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