package fr.lewon.dofus.bot.core.d2p.gfx

import fr.lewon.dofus.bot.core.d2p.AbstractLinkedD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.D2PIndex
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.io.File

object D2PItemsGfxAdapter : AbstractLinkedD2PUrlLoaderAdapter(false, -1) {

    override fun getId(filePath: String): Double {
        return Regex("(\\d+)\\..*?").find(filePath)?.destructured?.component1()?.toDouble()
            ?: -1.0
    }

    @Synchronized
    fun getItemGfxImageData(iconId: Double): ByteArray {
        return loadStream(iconId)
    }

    override fun doLoadStream(index: D2PIndex): ByteArray {
        val fileStream = ByteArrayReader(File(index.filePath).readBytes())
        fileStream.setPosition(index.offset)
        when {
            //index.filePath.endsWith(".png") ->
            //index.filePath.endsWith(".jpg") || index.filePath.endsWith(".jpeg") ->
            //index.key.endsWith(".swf") ->
        }
        return fileStream.readNBytes(index.length).also {
            File("C:/Dev/test.swf").writeBytes(it)
        }
    }

    private fun isValidPng(fileStream: ByteArrayReader): Boolean {
        val b1 = fileStream.readInt()
        val b2 = fileStream.readInt()
        println(b1)
        println(b2)
        return true
    }

    private fun isValidJPEG(fileStream: ByteArrayReader): Boolean {
        var b1 = fileStream.readUnsignedByte()
        var b2 = fileStream.readUnsignedByte()
        if ((b1 and 255) != 255 || (b2 and 255) != 216) {
            return false
        }
        fileStream.readAllBytes()
        fileStream.setPosition(fileStream.getPosition() - 2)
        b1 = fileStream.readUnsignedByte()
        b2 = fileStream.readUnsignedByte()
        return (b1 and 255) == 255 && (b2 and 255) == 217
    }

    private fun isValidSWF(fileStream: ByteArrayReader): Boolean {
        val header = fileStream.readString(3)
        return header == "CWS" || header == "FWS" || header == "ZWS"
    }
}