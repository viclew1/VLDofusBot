package fr.lewon.dofus.bot.core.d2p.sprite

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.DefineSpriteTag
import fr.lewon.dofus.bot.core.d2p.AbstractLinkedD2PUrlLoaderAdapter
import fr.lewon.dofus.bot.core.d2p.D2PIndex
import fr.lewon.dofus.bot.core.swl.SWL

object D2PBonesSpriteAdapter : AbstractLinkedD2PUrlLoaderAdapter(true, -1) {

    private val cache = HashMap<Double, DefineSprite?>()

    override fun getId(filePath: String): Double {
        return Regex("^(\\d+)\\.swl").find(filePath)?.destructured?.component1()?.toDouble()
            ?: -1.0
    }

    @Synchronized
    fun getBoneSprite(boneId: Double): DefineSprite? {
        return cache.getOrPut(boneId) { deserialize(loadStream(boneId)) }
    }

    override fun doLoadStream(index: D2PIndex): ByteArray {
        val fileStream = index.stream ?: error("Stream should be cached")
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
