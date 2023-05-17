package fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterCharacteristics : NetworkType() {
	var characteristics: ArrayList<CharacterCharacteristic> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		characteristics = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<CharacterCharacteristic>(stream.readUnsignedShort())
			item.deserialize(stream)
			characteristics.add(item)
		}
	}
}
