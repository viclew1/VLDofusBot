package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.action.fight.FightDispellableEffectExtendedInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight.GameActionMark
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightEffectTriggerCount
import fr.lewon.dofus.bot.sniffer.model.types.game.idol.Idol
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightSpectateMessage : NetworkMessage() {
	var effects: ArrayList<FightDispellableEffectExtendedInformations> = ArrayList()
	var marks: ArrayList<GameActionMark> = ArrayList()
	var gameTurn: Int = 0
	var fightStart: Int = 0
	var idols: ArrayList<Idol> = ArrayList()
	var fxTriggerCounts: ArrayList<GameFightEffectTriggerCount> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		effects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FightDispellableEffectExtendedInformations()
			item.deserialize(stream)
			effects.add(item)
		}
		marks = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameActionMark()
			item.deserialize(stream)
			marks.add(item)
		}
		gameTurn = stream.readVarShort().toInt()
		fightStart = stream.readInt().toInt()
		idols = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = Idol()
			item.deserialize(stream)
			idols.add(item)
		}
		fxTriggerCounts = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameFightEffectTriggerCount()
			item.deserialize(stream)
			fxTriggerCounts.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9527
}
