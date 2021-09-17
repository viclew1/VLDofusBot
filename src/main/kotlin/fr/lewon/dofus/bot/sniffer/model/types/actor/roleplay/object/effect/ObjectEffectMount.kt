package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.util.io.stream.BooleanByteWrapper
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ObjectEffectMount : ObjectEffect() {

    var id = -1L
    var expirationDate = -1L
    var model = -1
    lateinit var name: String
    lateinit var owner: String
    var level = -1
    var sex = false
    var isRideable = false
    var isFeconded = false
    var isFecondationReady = false
    var reproductionCount = -1
    var reproductionCountMax = -1
    var effects = ArrayList<ObjectEffectInteger>()
    var capacities = ArrayList<Int>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        val box = stream.readByte()
        sex = BooleanByteWrapper.getFlag(box, 0)
        isRideable = BooleanByteWrapper.getFlag(box, 1)
        isFeconded = BooleanByteWrapper.getFlag(box, 2)
        isFecondationReady = BooleanByteWrapper.getFlag(box, 3)
        id = stream.readVarLong()
        expirationDate = stream.readVarLong()
        model = stream.readVarInt()
        name = stream.readUTF()
        owner = stream.readUTF()
        level = stream.readByte().toInt()
        reproductionCount = stream.readVarInt()
        reproductionCountMax = stream.readVarInt()
        for (i in 0 until stream.readUnsignedShort()) {
            val effect = ObjectEffectInteger()
            effect.deserialize(stream)
            effects.add(effect)
        }
        for (i in 0 until stream.readUnsignedShort()) {
            capacities.add(stream.readVarInt())
        }
    }
}