package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.portal

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class PortalInformation : INetworkType {

    var portalId = 0
    var areaId = 0

    override fun deserialize(stream: ByteArrayReader) {
        portalId = stream.readInt()
        areaId = stream.readUnsignedShort()
    }
}