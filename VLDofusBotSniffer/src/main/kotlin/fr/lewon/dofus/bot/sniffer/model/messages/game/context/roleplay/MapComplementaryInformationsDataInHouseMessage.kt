package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightCommonInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightStartingPositions
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.house.HouseInformationsInside
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.MapObstacle
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.StatedElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MapComplementaryInformationsDataInHouseMessage : MapComplementaryInformationsDataMessage() {
	lateinit var currentHouse: HouseInformationsInside
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		currentHouse = HouseInformationsInside()
		currentHouse.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 6407
}
