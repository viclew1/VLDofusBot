package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightOptionsInformations : NetworkType() {
	var isSecret: Boolean = false
	var isRestrictedToPartyOnly: Boolean = false
	var isClosed: Boolean = false
	var isAskingForHelp: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		isSecret = BooleanByteWrapper.getFlag(_box0, 0)
		isRestrictedToPartyOnly = BooleanByteWrapper.getFlag(_box0, 1)
		isClosed = BooleanByteWrapper.getFlag(_box0, 2)
		isAskingForHelp = BooleanByteWrapper.getFlag(_box0, 3)
	}
}
