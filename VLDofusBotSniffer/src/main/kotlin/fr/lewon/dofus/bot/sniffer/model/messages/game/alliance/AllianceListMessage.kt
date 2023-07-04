package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.social.AllianceFactSheetInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceListMessage : NetworkMessage() {
	var alliances: ArrayList<AllianceFactSheetInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alliances = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AllianceFactSheetInformation()
			item.deserialize(stream)
			alliances.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1245
}
