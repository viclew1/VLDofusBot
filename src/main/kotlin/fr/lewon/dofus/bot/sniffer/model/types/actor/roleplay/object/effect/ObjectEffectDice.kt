package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class ObjectEffectDice : ObjectEffect() {

    var diceNum = -1
    var diceSide = -1
    var diceConst = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        diceNum = stream.readVarInt()
        diceSide = stream.readVarInt()
        diceConst = stream.readVarInt()
    }
}