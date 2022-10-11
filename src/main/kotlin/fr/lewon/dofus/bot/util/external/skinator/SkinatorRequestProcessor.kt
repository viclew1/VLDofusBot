package fr.lewon.dofus.bot.util.external.skinator

import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.EntityLook
import fr.lewon.dofus.bot.util.external.AbstractRequestProcessor
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import javax.imageio.ImageIO

object SkinatorRequestProcessor : AbstractRequestProcessor("https://www.dofusbook.net") {

    private const val FLASH_IMAGE_PREFIX = "/flash/"
    private const val FLASH_IMAGE_SUFFIX = "/full/1/250_325.png"

    override fun setRequestProperties(co: HttpURLConnection) {
        // Nothing
    }

    @Synchronized
    fun getSkinImage(entityLook: EntityLook): BufferedImage {
        val request = "$FLASH_IMAGE_PREFIX${getFlashVars(entityLook)}$FLASH_IMAGE_SUFFIX"
        val imageByteArray = get(request) ?: error("Couldn't find an image for this entity")
        return ImageIO.read(ByteArrayInputStream(imageByteArray))
    }

    private fun getFlashVars(entityLook: EntityLook): String {
        val realEntityLook = SkinatorUtil.getRealEntityLook(entityLook)
        val skinPart = realEntityLook.skins.joinToString(",")
        val colorsPart = getColors(realEntityLook)
            .mapIndexed { index, color -> index + 1 to color }
            .joinToString(",") { "${it.first}=${it.second}" }
        val lookPart = getLook(realEntityLook)[3]
        val flashVars = "{1|$skinPart|$colorsPart|$lookPart}"
        return flashVars.toCharArray().joinToString("") {
            Integer.toHexString(it.code).padStart(2, '0')
        }
    }

    private fun getLook(entityLook: EntityLook): List<String> {
        return listOf("1", "${entityLook.skins[0]}", "", "${entityLook.scales[0]}")
    }

    private fun getColors(entityLook: EntityLook): List<String> {
        return entityLook.indexedColors.map {
            val uColor = it and 16777215
            "#${Integer.toHexString(uColor)}"
        }
    }

}