package fr.lewon.dofus.bot.sniffer.model.messages.game.character.debt

import fr.lewon.dofus.bot.sniffer.model.types.game.character.debt.DebtInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class DebtsUpdateMessage : NetworkMessage() {
	var action: Int = 0
	var debts: ArrayList<DebtInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		action = stream.readUnsignedByte().toInt()
		debts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<DebtInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			debts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7589
}
