package fr.lewon.dofus.bot.gui.overlay.line

import java.awt.Graphics
import java.awt.Graphics2D

class OverlayTextLine(private val text: String, private val height: Int) : OverlayInfoLine() {

    override fun getHeight(g: Graphics): Int {
        return height
    }

    override fun getWidth(g: Graphics): Int {
        return g.fontMetrics.stringWidth(text)
    }

    override fun draw(g2: Graphics2D, x: Int, y: Int) {
        g2.drawString(text, x + 5, y + height)
    }
}