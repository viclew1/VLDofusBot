package fr.lewon.dofus.bot.sniffer.model.messages.game.nuggets

import fr.lewon.dofus.bot.sniffer.model.types.game.nuggets.NuggetsBeneficiary
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class NuggetsDistributionMessage : NetworkMessage() {
	var beneficiaries: ArrayList<NuggetsBeneficiary> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		beneficiaries = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = NuggetsBeneficiary()
			item.deserialize(stream)
			beneficiaries.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 3712
}
