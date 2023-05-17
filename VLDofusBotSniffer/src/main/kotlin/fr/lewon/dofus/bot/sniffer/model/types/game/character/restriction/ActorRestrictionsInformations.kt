package fr.lewon.dofus.bot.sniffer.model.types.game.character.restriction

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ActorRestrictionsInformations : NetworkType() {
	var cantBeAggressed: Boolean = false
	var cantBeChallenged: Boolean = false
	var cantTrade: Boolean = false
	var cantBeAttackedByMutant: Boolean = false
	var cantRun: Boolean = false
	var forceSlowWalk: Boolean = false
	var cantMinimize: Boolean = false
	var cantMove: Boolean = false
	var cantAggress: Boolean = false
	var cantChallenge: Boolean = false
	var cantExchange: Boolean = false
	var cantAttack: Boolean = false
	var cantChat: Boolean = false
	var cantUseObject: Boolean = false
	var cantUseTaxCollector: Boolean = false
	var cantUseInteractive: Boolean = false
	var cantSpeakToNPC: Boolean = false
	var cantChangeZone: Boolean = false
	var cantAttackMonster: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		cantBeAggressed = BooleanByteWrapper.getFlag(_box0, 0)
		cantBeChallenged = BooleanByteWrapper.getFlag(_box0, 1)
		cantTrade = BooleanByteWrapper.getFlag(_box0, 2)
		cantBeAttackedByMutant = BooleanByteWrapper.getFlag(_box0, 3)
		cantRun = BooleanByteWrapper.getFlag(_box0, 4)
		forceSlowWalk = BooleanByteWrapper.getFlag(_box0, 5)
		cantMinimize = BooleanByteWrapper.getFlag(_box0, 6)
		cantMove = BooleanByteWrapper.getFlag(_box0, 7)
		val _box1 = stream.readByte()
		cantAggress = BooleanByteWrapper.getFlag(_box1, 0)
		cantChallenge = BooleanByteWrapper.getFlag(_box1, 1)
		cantExchange = BooleanByteWrapper.getFlag(_box1, 2)
		cantAttack = BooleanByteWrapper.getFlag(_box1, 3)
		cantChat = BooleanByteWrapper.getFlag(_box1, 4)
		cantUseObject = BooleanByteWrapper.getFlag(_box1, 5)
		cantUseTaxCollector = BooleanByteWrapper.getFlag(_box1, 6)
		cantUseInteractive = BooleanByteWrapper.getFlag(_box1, 7)
		val _box2 = stream.readByte()
		cantSpeakToNPC = BooleanByteWrapper.getFlag(_box2, 0)
		cantChangeZone = BooleanByteWrapper.getFlag(_box2, 1)
		cantAttackMonster = BooleanByteWrapper.getFlag(_box2, 2)
	}
}
