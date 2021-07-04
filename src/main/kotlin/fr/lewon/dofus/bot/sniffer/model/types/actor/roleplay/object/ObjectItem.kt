package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect.ObjectEffect
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class ObjectItem : Item() {

    var position = -1
    var objectGID = -1
    var effects = ArrayList<ObjectEffect>()
    var objectUID = -1
    var quantity = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        position = stream.readShort()
        objectGID = stream.readVarShort()
        for (i in 0 until stream.readShort()) {
            val objectItem = TypeManager.getInstance<ObjectEffect>(stream.readShort())
            objectItem.deserialize(stream)
            effects.add(objectItem)
        }
        objectUID = stream.readVarInt()
        quantity = stream.readVarInt()
    }
}