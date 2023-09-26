package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightCommonInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightStartingPositions
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.MapObstacle
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.StatedElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapComplementaryInformationsDataMessage : NetworkMessage() {
	var subAreaId: Int = 0
	var mapId: Double = 0.0
	var houses: ArrayList<HouseInformations> = ArrayList()
	var actors: ArrayList<GameRolePlayActorInformations> = ArrayList()
	var interactiveElements: ArrayList<InteractiveElement> = ArrayList()
	var statedElements: ArrayList<StatedElement> = ArrayList()
	var obstacles: ArrayList<MapObstacle> = ArrayList()
	var fights: ArrayList<FightCommonInformations> = ArrayList()
	var hasAggressiveMonsters: Boolean = false
	lateinit var fightStartPositions: FightStartingPositions
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subAreaId = stream.readVarShort().toInt()
		mapId = stream.readDouble().toDouble()
		houses = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<HouseInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			houses.add(item)
		}
		actors = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameRolePlayActorInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			actors.add(item)
		}
		interactiveElements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<InteractiveElement>(stream.readUnsignedShort())
			item.deserialize(stream)
			interactiveElements.add(item)
		}
		statedElements = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = StatedElement()
			item.deserialize(stream)
			statedElements.add(item)
		}
		obstacles = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MapObstacle()
			item.deserialize(stream)
			obstacles.add(item)
		}
		fights = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FightCommonInformations()
			item.deserialize(stream)
			fights.add(item)
		}
		hasAggressiveMonsters = stream.readBoolean()
		fightStartPositions = FightStartingPositions()
		fightStartPositions.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 7827
}
