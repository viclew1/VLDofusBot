package fr.lewon.dofus.bot.sniffer.model.types.fight.charac

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightCharacteristics : INetworkType {

    lateinit var characteristics: CharacterCharacteristics
    var summoner = 0.0
    var summoned = false
    var invisibilityState = 0

    override fun deserialize(stream: ByteArrayReader) {
        characteristics = CharacterCharacteristics()
        characteristics.deserialize(stream)
        summoner = stream.readDouble()
        summoned = stream.readBoolean()
        invisibilityState = stream.readByte().toInt()
    }
}