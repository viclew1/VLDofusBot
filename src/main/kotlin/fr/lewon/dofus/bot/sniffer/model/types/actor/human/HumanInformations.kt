package fr.lewon.dofus.bot.sniffer.model.types.actor.human

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.human.options.HumanOption
import fr.lewon.dofus.bot.sniffer.model.types.actor.human.restrictions.ActorRestrictionsInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class HumanInformations : INetworkType {

    lateinit var restrictions: ActorRestrictionsInformations
    var sex = false
    var options = ArrayList<HumanOption>()

    override fun deserialize(stream: ByteArrayReader) {
        restrictions = ActorRestrictionsInformations()
        restrictions.deserialize(stream)
        sex = stream.readBoolean()
        for (i in 0 until stream.readUnsignedShort()) {
            val humanOption = TypeManager.getInstance<HumanOption>(stream.readUnsignedShort())
            humanOption.deserialize(stream)
            options.add(humanOption)
        }
    }

}