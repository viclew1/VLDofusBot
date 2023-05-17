package fr.lewon.dofus.bot.sniffer.model.types.game.look

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class EntityLook : NetworkType() {
	var bonesId: Int = 0
	var skins: ArrayList<Int> = ArrayList()
	var indexedColors: ArrayList<Int> = ArrayList()
	var scales: ArrayList<Int> = ArrayList()
	var subentities: ArrayList<SubEntity> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		bonesId = stream.readVarShort().toInt()
		skins = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			skins.add(item)
		}
		indexedColors = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			indexedColors.add(item)
		}
		scales = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			scales.add(item)
		}
		subentities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = SubEntity()
			item.deserialize(stream)
			subentities.add(item)
		}
	}
}
