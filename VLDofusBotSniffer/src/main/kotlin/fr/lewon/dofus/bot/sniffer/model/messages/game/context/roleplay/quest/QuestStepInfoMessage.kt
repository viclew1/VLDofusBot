package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.QuestActiveInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class QuestStepInfoMessage : NetworkMessage() {
	lateinit var infos: QuestActiveInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		infos = ProtocolTypeManager.getInstance<QuestActiveInformations>(stream.readUnsignedShort())
		infos.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4001
}
