package fr.lewon.dofus.bot.gui.util

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import javax.swing.ImageIcon


object ImageUtil {

    fun getScaledImage(imageData: ByteArray, w: Int, h: Int): BufferedImage {
        return getScaledImage(ImageIcon(imageData), w, h)
    }

    fun getScaledImage(imageData: ByteArray, w: Int): BufferedImage {
        val imageIcon = ImageIcon(imageData)
        val ratio = imageIcon.iconWidth.toFloat() / imageIcon.iconHeight.toFloat()
        val h = w.toFloat() / ratio
        return getScaledImage(imageIcon, w, h.toInt())
    }

    fun getScaledImageKeepHeight(imageData: ByteArray, h: Int): BufferedImage {
        val imageIcon = ImageIcon(imageData)
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

    fun getScaledImage(bufferedImage: BufferedImage, w: Int): BufferedImage {
        val ratio = bufferedImage.width.toFloat() / bufferedImage.height.toFloat()
        val h = w.toFloat() / ratio
        return getScaledImage(bufferedImage, w, h.toInt())
    }

    fun getScaledImageKeepHeight(bufferedImage: BufferedImage, h: Int): BufferedImage {
        val ratio = bufferedImage.width.toFloat() / bufferedImage.height.toFloat()
        val w = h.toFloat() * ratio
        return getScaledImage(bufferedImage, w.toInt(), h)
    }

    fun getScaledImage(bufferedImage: BufferedImage, w: Int, h: Int): BufferedImage {
        val resized = BufferedImage(w, h, bufferedImage.type)
        val g = resized.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.drawImage(bufferedImage, 0, 0, w, h, 0, 0, bufferedImage.width, bufferedImage.height, null)
        g.dispose()
        return resized
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