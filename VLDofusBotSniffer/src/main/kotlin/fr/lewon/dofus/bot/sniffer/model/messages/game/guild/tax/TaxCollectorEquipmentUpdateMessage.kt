package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristics
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorEquipmentUpdateMessage : NetworkMessage() {
	var uniqueId: Double = 0.0
	lateinit var obj: ObjectItem
	var added: Boolean = false
	lateinit var characteristics: CharacterCharacteristics
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uniqueId = stream.readDouble().toDouble()
		obj = ObjectItem()
		obj.deserialize(stream)
		added = stream.readBoolean()
		characteristics = CharacterCharacteristics()
		characteristics.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5895
}
