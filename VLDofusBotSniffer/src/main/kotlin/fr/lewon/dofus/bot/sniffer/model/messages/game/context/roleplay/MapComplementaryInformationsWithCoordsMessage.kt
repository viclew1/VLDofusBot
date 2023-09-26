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

open class MapComplementaryInformationsWithCoordsMessage : MapComplementaryInformationsDataMessage() {
	var worldX: Int = 0
	var worldY: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 2204
}
