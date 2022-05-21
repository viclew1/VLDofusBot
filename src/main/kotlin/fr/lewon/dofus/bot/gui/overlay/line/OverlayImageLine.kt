package fr.lewon.dofus.bot.gui.overlay.line

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class OverlayImageLine(private val image: BufferedImage) : OverlayInfoLine() {

    override fun getHeight(g: Graphics): Int {
        return image.height
    }

    override fun getWidth(g: Graphics): Int {
        return image.width
    }

    override fun draw(g2: Graphics2D, x: Int, y: Int) {
        g2.drawImage(image, x, y, null)
    }
}