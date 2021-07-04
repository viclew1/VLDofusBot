package fr.lewon.dofus.bot.sniffer.model.types.hunt

import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.model.hint.PhorrorHint
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class TreasureHuntStepFollowDirectionToHint : TreasureHuntStep() {

    lateinit var direction: Direction
    var npcId: Int = -1

    override fun deserialize(stream: ByteArrayReader) {
        val directionInt = stream.readByte().toInt()
        direction = Direction.fromInt(directionInt) ?: error("Unknown direction : $directionInt")
        npcId = stream.readVarShort()
    }

    override fun getHintLabel(): String {
        return PhorrorHint.name
    }
}