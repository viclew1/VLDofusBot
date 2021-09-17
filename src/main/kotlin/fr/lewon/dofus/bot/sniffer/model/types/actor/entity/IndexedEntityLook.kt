package fr.lewon.dofus.bot.sniffer.model.types.actor.entity

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class IndexedEntityLook : INetworkType {

    lateinit var look: EntityLook
    var index = -1

    override fun deserialize(stream: ByteArrayReader) {
        look = EntityLook()
        look.deserialize(stream)
        index = stream.readByte().toInt()
    }
}