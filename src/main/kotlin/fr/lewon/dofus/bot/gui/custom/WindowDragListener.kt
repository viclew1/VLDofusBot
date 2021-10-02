package fr.lewon.dofus.bot.gui.custom

import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

class WindowDragListener(private val window: Window) : MouseListener, MouseMotionListener {

    private var offset: Point = Point(0, 0)

    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent?) {
        offset = window.mousePosition
    }

    override fun mouseReleased(e: MouseEvent?) {
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }

    override fun mouseDragged(e: MouseEvent?) {
        val point = MouseInfo.getPointerInfo().location
        window.setLocation(
            point.x - offset.getX().toInt(),
            point.y - offset.getY().toInt()
        )
    }

    override fun mouseMoved(e: MouseEvent?) {
    }
}