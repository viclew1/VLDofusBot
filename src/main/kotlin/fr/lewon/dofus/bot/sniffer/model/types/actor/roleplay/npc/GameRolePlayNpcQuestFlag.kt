package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameRolePlayNpcQuestFlag : INetworkType {

    var questsToValidId = ArrayList<Int>()
    var questsToStartId = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            questsToValidId.add(stream.readVarShort())
        }
        for (i in 0 until stream.readUnsignedShort()) {
            questsToStartId.add(stream.readVarShort())
        }
    }
}