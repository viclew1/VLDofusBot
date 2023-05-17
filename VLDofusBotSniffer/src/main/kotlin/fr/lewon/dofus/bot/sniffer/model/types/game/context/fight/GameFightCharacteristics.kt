package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristics
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightCharacteristics : NetworkType() {
	lateinit var characteristics: CharacterCharacteristics
	var summoner: Double = 0.0
	var summoned: Boolean = false
	var invisibilityState: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		characteristics = CharacterCharacteristics()
		characteristics.deserialize(stream)
		summoner = stream.readDouble().toDouble()
		summoned = stream.readBoolean()
		invisibilityState = stream.readUnsignedByte().toInt()
	}
}
