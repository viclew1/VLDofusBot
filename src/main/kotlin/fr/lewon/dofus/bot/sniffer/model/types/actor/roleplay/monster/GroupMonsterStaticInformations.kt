package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GroupMonsterStaticInformations : INetworkType {

    lateinit var mainCreatureLightInfos: MonsterInGroupLightInformations
    var underlings = ArrayList<MonsterInGroupInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        mainCreatureLightInfos = MonsterInGroupLightInformations()
        mainCreatureLightInfos.deserialize(stream)
        for (i in 0 until stream.readUnsignedShort()) {
            val underling = MonsterInGroupInformations()
            underling.deserialize(stream)
            underlings.add(underling)
        }
    }
}