package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AnomalySubareaInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AnomalySubareaInformationResponseMessage : NetworkMessage() {
	var subareas: ArrayList<AnomalySubareaInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		subareas = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AnomalySubareaInformation()
			item.deserialize(stream)
			subareas.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4331
}
