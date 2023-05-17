package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockContentInformations : PaddockInformations() {
	var paddockId: Double = 0.0
	var worldX: Int = 0
	var worldY: Int = 0
	var mapId: Double = 0.0
	var subAreaId: Int = 0
	var abandonned: Boolean = false
	var mountsInformations: ArrayList<MountInformationsForPaddock> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		paddockId = stream.readDouble().toDouble()
		worldX = stream.readUnsignedShort().toInt()
		worldY = stream.readUnsignedShort().toInt()
		mapId = stream.readDouble().toDouble()
		subAreaId = stream.readVarShort().toInt()
		abandonned = stream.readBoolean()
		mountsInformations = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MountInformationsForPaddock()
			item.deserialize(stream)
			mountsInformations.add(item)
		}
	}
}
