package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightEntityInformation : GameFightFighterInformations() {

    var entityModel = 0
    var level = 0
    var masterId = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        entityModel = stream.readByte().toInt()
        level = stream.readVarShort()
        masterId = stream.readDouble()
    }
}