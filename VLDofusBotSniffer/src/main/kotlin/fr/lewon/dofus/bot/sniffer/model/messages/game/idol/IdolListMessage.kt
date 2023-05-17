package fr.lewon.dofus.bot.sniffer.model.messages.game.idol

import fr.lewon.dofus.bot.sniffer.model.types.game.idol.PartyIdol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdolListMessage : NetworkMessage() {
	var chosenIdols: ArrayList<Int> = ArrayList()
	var partyChosenIdols: ArrayList<Int> = ArrayList()
	var partyIdols: ArrayList<PartyIdol> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		chosenIdols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			chosenIdols.add(item)
		}
		partyChosenIdols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			partyChosenIdols.add(item)
		}
		partyIdols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PartyIdol>(stream.readUnsignedShort())
			item.deserialize(stream)
			partyIdols.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4298
}
