package fr.lewon.dofus.bot.sniffer.model.types.actor.entity

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class SubEntity : INetworkType {

    var bindingPointCategory = -1
    var bindingPointIndex = -1
    lateinit var entityLook: EntityLook

    override fun deserialize(stream: ByteArrayReader) {
        bindingPointCategory = stream.readByte().toInt()
        bindingPointIndex = stream.readByte().toInt()
        entityLook = EntityLook()
        entityLook.deserialize(stream)
    }
}
