package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractContactInformations : NetworkType() {
	var accountId: Int = 0
	lateinit var accountTag: AccountTagInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accountId = stream.readInt().toInt()
		accountTag = AccountTagInformation()
		accountTag.deserialize(stream)
	}
}
