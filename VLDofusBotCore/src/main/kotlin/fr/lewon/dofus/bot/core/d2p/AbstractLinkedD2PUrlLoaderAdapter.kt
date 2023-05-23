package fr.lewon.dofus.bot.core.d2p

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.io.File

abstract class AbstractLinkedD2PUrlLoaderAdapter(
    private val cacheStreams: Boolean,
    loaderHeader: Int
) : AbstractD2PUrlLoaderAdapter(loaderHeader) {

    private val indexes = HashMap<Double, D2PIndex>()

    override fun initStream(path: String) {
        val file = File(path)
        val stream = ByteArrayReader(file.readBytes())
        val vMax = stream.readUnsignedByte()
        val vMin = stream.readUnsignedByte()
        if (vMax != 2 || vMin != 1) {
            error("Invalid D2P file : $path")
        }
        stream.setPosition(file.length().toInt() - 24)
        val dataOffset = stream.readInt()
        val dataCount = stream.readInt()
        val indexOffset = stream.readInt()
        val indexCount = stream.readInt()
        val propertiesOffset = stream.readInt()
        val propertiesCount = stream.readInt()
        stream.setPosition(indexOffset)
        for (i in 0 until indexCount) {
            val indexKey = stream.readUTF()
            val fileOffset = stream.readInt()
            val fileLength = stream.readInt()
            val id = getId(indexKey)
            val index = if (cacheStreams) {
                D2PIndex(indexKey, fileOffset + dataOffset, fileLength, path, stream)
            } else {
                D2PIndex(indexKey, fileOffset + dataOffset, fileLength, path)
            }
            indexes[id] = index
        }
    }

    protected fun loadStream(id: Double): ByteArray {
        val index = indexes[id] ?: error("No index for id : $id")
        return doLoadStream(index)
    }

    protected abstract fun doLoadStream(index: D2PIndex): ByteArray

    protected abstract fun getId(filePath: String): Double

}