package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GroupMonsterStaticInformations : INetworkType {

    lateinit var mainCreatureLightInfos: MonsterInGroupLightInformations
    var underlings = ArrayList<MonsterInGroupInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        mainCreatureLightInfos = MonsterInGroupLightInformations()
        mainCreatureLightInfos.deserialize(stream)
        for (i in 0 until stream.readShort()) {
            val underling = MonsterInGroupInformations()
            underling.deserialize(stream)
            underlings.add(underling)
        }
    }
}