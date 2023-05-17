package fr.lewon.dofus.bot.sniffer.model.types.game.character.choice

import fr.lewon.dofus.bot.sniffer.model.types.game.character.AbstractCharacterInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CharacterRemodelingInformation : AbstractCharacterInformation() {
	var name: String = ""
	var breed: Int = 0
	var sex: Boolean = false
	var cosmeticId: Int = 0
	var colors: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		name = stream.readUTF()
		breed = stream.readUnsignedByte().toInt()
		sex = stream.readBoolean()
		cosmeticId = stream.readVarShort().toInt()
		colors = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			colors.add(item)
		}
	}
}
