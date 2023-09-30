package fr.lewon.dofus.bot.core.swl

import java.nio.ByteBuffer
import java.nio.ByteOrder

class SWL() {
    var version: Int = -1
    var frameRate: Int = -1
    lateinit var classes: ArrayList<String>
    lateinit var swfData: ByteArray

    /*
     * Based on https://github.com/balciseri/PyDofus/blob/master/pydofus/swl.py
     */
    fun deserialize(data: ByteArray) {
        val stream = data.inputStream()
        // FIXME: use ByteArrayStream?

        val byteHeader = stream.read() // char
        require(byteHeader == 76) { error("Invalid SWL file") }

        version = stream.read() // char
        frameRate = ByteBuffer.wrap(stream.readNBytes(4)).order(ByteOrder.BIG_ENDIAN).getInt() // uint32
        val classesCount = ByteBuffer.wrap(stream.readNBytes(4)).order(ByteOrder.BIG_ENDIAN).getInt() // int32
        classes = ArrayList(classesCount)
        for (i in 0 until classesCount) {
            val strLen = ByteBuffer.wrap(stream.readNBytes(2)).order(ByteOrder.BIG_ENDIAN).getShort().toInt() // uint16
            classes.add(String(stream.readNBytes(strLen))) // , Charsets.UTF_16
        }

        swfData = stream.readAllBytes()
    }
}
