package fr.lewon.dofus.bot.core.d2p.gfx

import fr.lewon.dofus.bot.core.d2p.AbstractLinkedD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.D2PIndex
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import java.io.File

object D2PItemsGfxAdapter : AbstractLinkedD2PUrlLoaderAdapter(false, -1) {

    override fun getId(filePath: String): Double {
        return Regex("(\\d+)\\.png").find(filePath)?.destructured?.component1()?.toDouble()
            ?: -1.0
    }

    @Synchronized
    fun getItemGfxImageData(iconId: Double): ByteArray {
        return loadStream(iconId)
    }

    override fun doLoadStream(index: D2PIndex): ByteArray {
        val fileStream = ByteArrayReader(File(index.filePath).readBytes())
        fileStream.setPosition(index.offset)
        return fileStream.readNBytes(index.length)
    }

}