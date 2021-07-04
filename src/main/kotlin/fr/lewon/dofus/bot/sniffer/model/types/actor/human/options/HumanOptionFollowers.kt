package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.IndexedEntityLook
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HumanOptionFollowers : HumanOption() {

    var followingCharactersLook = ArrayList<IndexedEntityLook>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readShort()) {
            val indexedEntityLook = IndexedEntityLook()
            indexedEntityLook.deserialize(stream)
            followingCharactersLook.add(indexedEntityLook)
        }
    }
}