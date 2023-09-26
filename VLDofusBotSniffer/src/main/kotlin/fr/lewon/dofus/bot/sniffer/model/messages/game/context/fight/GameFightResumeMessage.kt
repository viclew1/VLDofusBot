package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.action.fight.FightDispellableEffectExtendedInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight.GameActionMark
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightEffectTriggerCount
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightSpellCooldown
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightResumeMessage : GameFightSpectateMessage() {
	var spellCooldowns: ArrayList<GameFightSpellCooldown> = ArrayList()
	var summonCount: Int = 0
	var bombCount: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		spellCooldowns = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameFightSpellCooldown()
			item.deserialize(stream)
			spellCooldowns.add(item)
		}
		summonCount = stream.readUnsignedByte().toInt()
		bombCount = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 9492
}
