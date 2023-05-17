package fr.lewon.dofus.bot.core.d2p.elem

import fr.lewon.dofus.bot.core.d2p.AbstractD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementData
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementFactory
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.io.File

object D2PElementsAdapter : AbstractD2PUrlLoaderAdapter(69) {

    private var fileVersion = -1
    private val elementsIndexById = HashMap<Int, ElementIndex>()
    private val elementById = HashMap<Int, GraphicalElementData>()

    @Synchronized
    fun getElement(elementId: Int): GraphicalElementData {
        return elementById[elementId] ?: readElement(elementId)
    }

    override fun initStream(path: String) {
        val stream = getFileStream(path)
        val header = stream.readByte().toInt()
        if (header != loaderHeader) {
            error("Unknown file format for elements : $header")
        }
        fileVersion = stream.readByte().toInt()
        val elementsCount = stream.readInt()
        var skypLen = 0
        for (i in 0 until elementsCount) {
            if (fileVersion >= 9) {
                skypLen = stream.readUnsignedShort()
            }
            val elementId = stream.readInt()
            elementsIndexById[elementId] = ElementIndex(path, stream.getPosition())
            if (fileVersion <= 8) {
                readElement(elementId)
            } else {
                stream.skip(skypLen - 4)
            }
        }
        if (fileVersion >= 8) {
            val gfxCount = stream.readInt()
            val jpgMap = HashMap<Int, Boolean>()
            for (i in 0 until gfxCount) {
                val gfxId = stream.readInt()
                jpgMap[gfxId] = true
            }
        }
    }

    private fun getFileStream(path: String): ByteArrayReader {
        val file = File(path)
        if (!file.exists()) {
            error("Elements file not found : $path")
        }
        return loadFromData(file.readBytes())
    }

    private fun readElement(elementId: Int): GraphicalElementData {
        val elementIndex = elementsIndexById[elementId] ?: error("Element not found : $elementId")
        val stream = getFileStream(elementIndex.fileName)
        stream.setPosition(elementIndex.index)
        return GraphicalElementFactory.getGraphicalElementData(elementId, stream.readUnsignedByte())
            .also { it.deserialize(stream, fileVersion) }
            .also { elementById[elementId] = it }
    }

    private class ElementIndex(val fileName: String, val index: Int)

}