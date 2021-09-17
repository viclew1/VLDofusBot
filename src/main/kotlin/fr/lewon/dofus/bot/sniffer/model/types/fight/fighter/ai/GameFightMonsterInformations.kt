package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GameFightMonsterInformations : GameFightAIInformations() {

    var creatureGenericId = 0
    var creatureGrade = 0
    var creatureLevel = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        creatureGenericId = stream.readVarShort()
        creatureGrade = stream.readByte().toInt()
        creatureLevel = stream.readUnsignedShort()
    }
}