package fr.lewon.dofus.bot.sniffer.model.types.actor.human.restrictions

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.BooleanByteWrapper
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class ActorRestrictionsInformations : INetworkType {

    var cantBeAggressed = false
    var cantBeChallenged = false
    var cantTrade = false
    var cantBeAttackedByMutant = false
    var cantRun = false
    var forceSlowWalk = false
    var cantMinimize = false
    var cantMove = false
    var cantAggress = false
    var cantChallenge = false
    var cantExchange = false
    var cantAttack = false
    var cantChat = false
    var cantBeMerchant = false
    var cantUseObject = false
    var cantUseTaxCollector = false
    var cantUseInteractive = false
    var cantSpeakToNPC = false
    var cantChangeZone = false
    var cantAttackMonster = false
    var cantWalk8Directions = false

    override fun deserialize(stream: ByteArrayReader) {
        val box0 = stream.readByte()
        val box1 = stream.readByte()
        val box2 = stream.readByte()
        cantBeAggressed = BooleanByteWrapper.getFlag(box0, 0)
        cantBeChallenged = BooleanByteWrapper.getFlag(box0, 1)
        cantTrade = BooleanByteWrapper.getFlag(box0, 2)
        cantBeAttackedByMutant = BooleanByteWrapper.getFlag(box0, 3)
        cantRun = BooleanByteWrapper.getFlag(box0, 4)
        forceSlowWalk = BooleanByteWrapper.getFlag(box0, 5)
        cantMinimize = BooleanByteWrapper.getFlag(box0, 6)
        cantMove = BooleanByteWrapper.getFlag(box0, 7)
        cantAggress = BooleanByteWrapper.getFlag(box1, 0)
        cantChallenge = BooleanByteWrapper.getFlag(box1, 1)
        cantExchange = BooleanByteWrapper.getFlag(box1, 2)
        cantAttack = BooleanByteWrapper.getFlag(box1, 3)
        cantChat = BooleanByteWrapper.getFlag(box1, 4)
        cantBeMerchant = BooleanByteWrapper.getFlag(box1, 5)
        cantUseObject = BooleanByteWrapper.getFlag(box1, 6)
        cantUseTaxCollector = BooleanByteWrapper.getFlag(box1, 7)
        cantUseInteractive = BooleanByteWrapper.getFlag(box2, 0)
        cantSpeakToNPC = BooleanByteWrapper.getFlag(box2, 1)
        cantChangeZone = BooleanByteWrapper.getFlag(box2, 2)
        cantAttackMonster = BooleanByteWrapper.getFlag(box2, 3)
        cantWalk8Directions = BooleanByteWrapper.getFlag(box2, 4)
    }
}