package fr.lewon.dofus.bot.util.io.d2p

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import java.io.File

object D2PUtil {

    val indexes = HashMap<Double, D2PIndex>()
    private val properties = HashMap<String, String>()

    fun initStream(path: String): ByteArrayReader? {
        var filePath = path
        var file: File? = File(filePath)
        var stream: ByteArrayReader? = null
        while (file != null && file.exists()) {
            stream = ByteArrayReader(file.readBytes())
            val vMax = stream.readByte().toInt()
            val vMin = stream.readByte().toInt()
            if (vMax != 2 || vMin != 1) {
                return null
            }
            stream.setPosition(file.length().toInt() - 24)
            val dataOffset = stream.readInt()
            val dataCount = stream.readInt()
            val indexOffset = stream.readInt()
            val indexCount = stream.readInt()
            val propertiesOffset = stream.readInt()
            val propertiesCount = stream.readInt()
            stream.setPosition(propertiesOffset)
            file = null
            for (i in 0 until propertiesCount) {
                val propertyName = stream.readUTF()
                val propertyValue = stream.readUTF()
                properties[propertyName] = propertyValue
                if (propertyName == "link") {
                    val idx = filePath.lastIndexOf("/");
                    filePath = if (idx != -1) {
                        filePath.substring(0, idx) + "/" + propertyValue
                    } else {
                        propertyValue
                    }
                    file = File(filePath)
                }
            }
            stream.setPosition(indexOffset)
            for (i in 0 until indexCount) {
                filePath = stream.readUTF()
                val fileOffset = stream.readInt()
                val fileLength = stream.readInt()
                indexes[getMapId(filePath)] = D2PIndex(fileOffset + dataOffset, fileLength, stream)
            }
        }
        return stream
    }

    private fun getMapId(filePath: String): Double {
        return Regex(".*?/([0-9]+)\\.dlm")
            .find(filePath)
            ?.destructured
            ?.component1()
            ?.toDouble()
            ?: error("Invalid key")
    }

}