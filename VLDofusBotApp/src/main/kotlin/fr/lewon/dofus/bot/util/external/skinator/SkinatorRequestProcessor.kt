package fr.lewon.dofus.bot.util.external.skinator

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
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
    fun getSkinImage(flashVars: String): BufferedImage {
        val request = "$FLASH_IMAGE_PREFIX$flashVars$FLASH_IMAGE_SUFFIX"
        val imageByteArray = get(request) ?: error("Couldn't find an image for this entity")
        return ImageIO.read(ByteArrayInputStream(imageByteArray))
    }

    fun getFlashVars(entityLook: EntityLook): String {
        val realEntityLook = getRealEntityLook(entityLook)
        val skinPart = realEntityLook.skins.joinToString(",")
        val colorsPart = getColors(realEntityLook)
            .mapIndexed { index, color -> index + 1 to color }
            .joinToString(",") { "${it.first}=${it.second}" }
        val lookPart = "140"
        val flashVars = "{1|$skinPart|$colorsPart|$lookPart}"
        return flashVars.toCharArray().joinToString("") {
            Integer.toHexString(it.code).padStart(2, '0')
        }
    }

    private fun getRealEntityLook(entityLook: EntityLook): EntityLook {
        if (entityLook.bonesId > 10 && entityLook.subentities.isNotEmpty()) {
            return getRealEntityLook(entityLook.subentities.first().subEntityLook)
        }
        return entityLook
    }

    private fun getColors(entityLook: EntityLook): List<String> {
        return entityLook.indexedColors.map {
            val uColor = it and 16777215
            "#${Integer.toHexString(uColor)}"
        }
    }

}