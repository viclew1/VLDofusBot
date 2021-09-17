package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class AlternativeMonstersInGroupLightInformations : INetworkType {

    var playerCount = -1
    var monsters = ArrayList<MonsterInGroupLightInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        playerCount = stream.readInt()
        for (i in 0 until stream.readUnsignedShort()) {
            val monster = MonsterInGroupLightInformations()
            monster.deserialize(stream)
            monsters.add(monster)
        }
    }
}