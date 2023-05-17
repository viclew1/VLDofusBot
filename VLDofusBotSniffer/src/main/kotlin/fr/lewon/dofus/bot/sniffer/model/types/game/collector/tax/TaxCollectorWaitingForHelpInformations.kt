package fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.fight.ProtectedEntityWaitingForHelpInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorWaitingForHelpInformations : TaxCollectorComplementaryInformations() {
	lateinit var waitingForHelpInfo: ProtectedEntityWaitingForHelpInfo
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		waitingForHelpInfo = ProtectedEntityWaitingForHelpInfo()
		waitingForHelpInfo.deserialize(stream)
	}
}
