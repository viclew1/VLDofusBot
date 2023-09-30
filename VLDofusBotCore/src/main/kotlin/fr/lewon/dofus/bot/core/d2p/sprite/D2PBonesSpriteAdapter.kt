package fr.lewon.dofus.bot.core.d2p.sprite

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.DefineSpriteTag
import fr.lewon.dofus.bot.core.d2p.AbstractLinkedD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.D2PIndex
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.swl.SWL
import java.io.File

object D2PBonesSpriteAdapter : AbstractLinkedD2PUrlLoaderAdapter(false, -1) {

    override fun getId(filePath: String): Double {
        return Regex("^(\\d+)\\.swl").find(filePath)?.destructured?.component1()?.toDouble()
            ?: -1.0
    }

    @Synchronized
    fun getBoneSprite(boneId: Double): DefineSprite? {
        // TODO: cache results here?
        return deserialize(loadStream(boneId))
    }

    override fun doLoadStream(index: D2PIndex): ByteArray {
        val fileStream = ByteArrayReader(File(index.filePath).readBytes())
        fileStream.setPosition(index.offset)
        return fileStream.readNBytes(index.length)
    }

    private fun deserialize(data: ByteArray): DefineSprite? {
        val swl = SWL()
        swl.deserialize(data)
        val swf = SWF(swl.swfData.inputStream(), true)
        return getDefineSprite(swf, "AnimState0_0")
    }

    private fun getDefineSprite(swf: SWF, tagName: String): DefineSprite? {
        val tagRegex = Regex("^DefineSprite \\(\\d+: ${tagName}\\)$")
        return swf.tags.find { it.name.contains(tagRegex) }?.let { DefineSprite(it as DefineSpriteTag) }
    }
}
