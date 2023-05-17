package fr.lewon.dofus.bot.sniffer.model.messages.game.character.creation

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCreationRequestMessage : NetworkMessage() {
	var name: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	var colors: ArrayList<Int> = ArrayList()
	var cosmeticId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		colors = ArrayList()
		for (i in 0 until 5) {
			val item = stream.readInt().toInt()
			colors.add(item)
		}
		cosmeticId = stream.readVarShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 1393
}
