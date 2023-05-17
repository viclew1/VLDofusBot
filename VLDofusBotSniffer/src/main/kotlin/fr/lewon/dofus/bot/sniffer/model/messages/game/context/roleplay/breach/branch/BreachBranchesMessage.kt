package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.breach.branch

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.breach.ExtendedBreachBranch
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BreachBranchesMessage : NetworkMessage() {
	var branches: ArrayList<ExtendedBreachBranch> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		branches = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<ExtendedBreachBranch>(stream.readUnsignedShort())
			item.deserialize(stream)
			branches.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 6699
}
