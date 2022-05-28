package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.io.MouseUtil
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

abstract class AbstractOverlayPanel(protected open val overlay: AbstractOverlay) : JPanel() {

    init {
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                onHover(e.point)
            }

            override fun mouseDragged(e: MouseEvent) {
                mouseMoved(e)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1) {
                    sendClickToGame(e.point)
                }
            }
        })
    }

    private fun sendClickToGame(mouseLocation: Point) {
        val absoluteLocation = PointAbsolute(
            mouseLocation.x + overlay.overlayBounds.x,
            mouseLocation.y + overlay.overlayBounds.y
        )
        MouseUtil.leftClick(overlay.gameInfo, absoluteLocation)
    }

    abstract fun onHover(mouseLocation: Point)

    override fun paintComponent(g: Graphics) {
        rootPane.updateUI()
        super.paintComponent(g)
        val oldColor = g.color
        g.color = Color.BLACK
        drawBackground(g)
        drawOverlay(g)
        g.color = oldColor
    }

    protected abstract fun drawBackground(g: Graphics)

    protected abstract fun drawOverlay(g: Graphics)

}
