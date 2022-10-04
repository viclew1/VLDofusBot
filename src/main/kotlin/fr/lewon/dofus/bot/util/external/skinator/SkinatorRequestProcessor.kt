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

    fun getSkinImage(entityLook: EntityLook): BufferedImage {
        val request = "$FLASH_IMAGE_PREFIX${getFlashVars(entityLook)}$FLASH_IMAGE_SUFFIX"
        val imageByteArray = get(request) ?: error("Couldn't find an image for this entity")
        return ImageIO.read(ByteArrayInputStream(imageByteArray))
    }

    fun getFlashVars(entityLook: EntityLook): String {
        val skinPart = entityLook.skins.joinToString(",")
        val colorsPart = getColors(entityLook).subList(0, 5)
            .mapIndexed { index, color -> index + 1 to color }
            .joinToString(",") { "${it.first}=${it.second}" }
        val lookPart = getLook(entityLook)[3]
        val flashVars = "{1|$skinPart|$colorsPart|$lookPart}"
        println(flashVars)
        return flashVars.toCharArray().joinToString("") {
            Integer.toHexString(it.code).padStart(2, '0')
        }.also { println(it) }
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