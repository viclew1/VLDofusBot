package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.prism

import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.ObjectItem
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class AllianceInsiderPrismInformation : PrismInformation() {

    var lastTimeSlotModificationDate = -1
    var lastTimeSlotModificationAuthorGuildId = -1
    var lastTimeSlotModificationAuthorId = -1L
    lateinit var lastTimeSlotModificationAuthorName: String
    var modulesObjects = ArrayList<ObjectItem>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        lastTimeSlotModificationDate = stream.readInt()
        lastTimeSlotModificationAuthorGuildId = stream.readVarInt()
        lastTimeSlotModificationAuthorId = stream.readVarLong()
        lastTimeSlotModificationAuthorName = stream.readUTF()
        for (i in 0 until stream.readUnsignedShort()) {
            val objectItem = ObjectItem()
            objectItem.deserialize(stream)
            modulesObjects.add(objectItem)
        }
    }
}