package fr.lewon.dofus.bot.sniffer.model.types.actor.entity

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class EntityLook : INetworkType {

    var bonesId = -1
    var skins = ArrayList<Int>()
    var indexedColors = ArrayList<Int>()
    var scales = ArrayList<Int>()
    var subEntities = ArrayList<SubEntity>()

    override fun deserialize(stream: ByteArrayReader) {
        bonesId = stream.readVarShort()
        for (i in 0 until stream.readShort()) {
            skins.add(stream.readVarShort())
        }
        for (i in 0 until stream.readShort()) {
            indexedColors.add(stream.readInt())
        }
        for (i in 0 until stream.readShort()) {
            scales.add(stream.readVarShort())
        }
        for (i in 0 until stream.readShort()) {
            val subEntity = SubEntity()
            subEntity.deserialize(stream)
            subEntities.add(subEntity)
        }
    }

}
