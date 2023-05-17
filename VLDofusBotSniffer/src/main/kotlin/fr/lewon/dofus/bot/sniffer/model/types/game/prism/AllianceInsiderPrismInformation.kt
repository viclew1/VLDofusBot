package fr.lewon.dofus.bot.sniffer.model.types.game.prism

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceInsiderPrismInformation : PrismInformation() {
	lateinit var moduleObject: ObjectItem
	var moduleType: Int = 0
	lateinit var cristalObject: ObjectItem
	var cristalType: Int = 0
	var cristalNumberLeft: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		moduleObject = ObjectItem()
		moduleObject.deserialize(stream)
		moduleType = stream.readInt().toInt()
		cristalObject = ObjectItem()
		cristalObject.deserialize(stream)
		cristalType = stream.readInt().toInt()
		cristalNumberLeft = stream.readInt().toInt()
	}
}
