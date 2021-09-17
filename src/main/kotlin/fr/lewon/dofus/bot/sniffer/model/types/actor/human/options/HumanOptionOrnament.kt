package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class HumanOptionOrnament : HumanOption() {

    var ornamentId = -1
    var level = -1
    var leagueId = -1
    var ladderPosition = -1

    override fun deserialize(stream: ByteArrayReader) {
        ornamentId = stream.readVarShort()
        level = stream.readVarShort()
        leagueId = stream.readVarShort()
        ladderPosition = stream.readInt()
    }
}