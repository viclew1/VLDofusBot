package fr.lewon.dofus.bot.gui.util

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.net.URL
import javax.swing.ImageIcon

object ImageUtil {

    fun getScaledImage(imageUrl: URL, w: Int, h: Int): BufferedImage {
        return getScaledImage(ImageIcon(imageUrl), w, h)
    }

    fun getScaledImage(imageUrl: URL, w: Int): BufferedImage {
        val imageIcon = ImageIcon(imageUrl)
        val ratio = imageIcon.iconWidth.toFloat() / imageIcon.iconHeight.toFloat()
        val h = w.toFloat() / ratio
        return getScaledImage(imageIcon, w, h.toInt())
    }

    fun getScaledImageKeepHeight(imageUrl: URL, h: Int): BufferedImage {
        val imageIcon = ImageIcon(imageUrl)
        val ratio = imageIcon.iconWidth.toFloat() / imageIcon.iconHeight.toFloat()
        val w = h.toFloat() * ratio
        return getScaledImage(imageIcon, w.toInt(), h)
    }

    fun getScaledImage(imageIcon: ImageIcon, w: Int, h: Int): BufferedImage {
        val resizedImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = resizedImg.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.drawImage(imageIcon.image, 0, 0, w, h, null)
        g2.dispose()
        return resizedImg
    }

    fun blurImage(bgImg: BufferedImage, blurRatio: Float = 1f): BufferedImage {
        var data = floatArrayOf(
            0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f,
            0.0625f, 0.125f, 0.0625f
        )
        data = data.map { it * blurRatio }.toFloatArray()
        val kernel = Kernel(3, 3, data)
        val convolve = ConvolveOp(
            kernel, ConvolveOp.EDGE_NO_OP,
            null
        )
        val blurredImg = BufferedImage(bgImg.width, bgImg.height, BufferedImage.TYPE_INT_ARGB)
        convolve.filter(bgImg, blurredImg)
        return blurredImg
    }

}