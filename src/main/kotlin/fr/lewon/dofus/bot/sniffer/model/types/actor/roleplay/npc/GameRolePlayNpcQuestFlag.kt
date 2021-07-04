package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayNpcQuestFlag : INetworkType {

    var questsToValidId = ArrayList<Int>()
    var questsToStartId = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readShort()) {
            questsToValidId.add(stream.readVarShort())
        }
        for (i in 0 until stream.readShort()) {
            questsToStartId.add(stream.readVarShort())
        }
    }
}