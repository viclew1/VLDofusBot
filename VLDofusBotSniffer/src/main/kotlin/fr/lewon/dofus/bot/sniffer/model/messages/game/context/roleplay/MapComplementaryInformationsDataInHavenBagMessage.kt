package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.character.CharacterMinimalInformations
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

open class MapComplementaryInformationsDataInHavenBagMessage : MapComplementaryInformationsDataMessage() {
	lateinit var ownerInformations: CharacterMinimalInformations
	var theme: Int = 0
	var roomId: Int = 0
	var maxRoomId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		ownerInformations = CharacterMinimalInformations()
		ownerInformations.deserialize(stream)
		theme = stream.readUnsignedByte().toInt()
		roomId = stream.readUnsignedByte().toInt()
		maxRoomId = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 5660
}
