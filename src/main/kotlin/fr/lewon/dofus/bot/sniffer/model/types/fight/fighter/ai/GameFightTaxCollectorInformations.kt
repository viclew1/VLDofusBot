package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightTaxCollectorInformations : GameFightAIInformations() {

    var firstNameId = 0
    var lastNameId = 0
    var level = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        firstNameId = stream.readVarShort()
        lastNameId = stream.readVarShort()
        level = stream.readByte().toInt()
    }
}