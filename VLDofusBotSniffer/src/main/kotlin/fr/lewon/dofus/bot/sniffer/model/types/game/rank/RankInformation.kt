package fr.lewon.dofus.bot.sniffer.model.types.game.rank

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RankInformation : RankMinimalInformation() {
	var order: Int = 0
	var gfxId: Int = 0
	var modifiable: Boolean = false
	var rights: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		order = stream.readVarInt().toInt()
		gfxId = stream.readVarInt().toInt()
		modifiable = stream.readBoolean()
		rights = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			rights.add(item)
		}
	}
}
