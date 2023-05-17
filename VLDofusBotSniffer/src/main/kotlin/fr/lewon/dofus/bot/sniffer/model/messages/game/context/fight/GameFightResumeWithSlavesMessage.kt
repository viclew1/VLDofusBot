package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.action.fight.FightDispellableEffectExtendedInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight.GameActionMark
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightEffectTriggerCount
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightResumeSlaveInfo
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightSpellCooldown
import fr.lewon.dofus.bot.sniffer.model.types.game.idol.Idol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightResumeWithSlavesMessage : GameFightResumeMessage() {
	var slavesInfo: ArrayList<GameFightResumeSlaveInfo> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		slavesInfo = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameFightResumeSlaveInfo()
			item.deserialize(stream)
			slavesInfo.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 1237
}
