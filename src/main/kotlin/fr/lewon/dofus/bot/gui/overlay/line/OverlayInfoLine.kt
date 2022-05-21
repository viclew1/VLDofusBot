package fr.lewon.dofus.bot.gui.overlay.line

import java.awt.Graphics
import java.awt.Graphics2D

abstract class OverlayInfoLine {

    abstract fun getHeight(g: Graphics): Int

    abstract fun getWidth(g: Graphics): Int

    abstract fun draw(g2: Graphics2D, x: Int, y: Int)

}