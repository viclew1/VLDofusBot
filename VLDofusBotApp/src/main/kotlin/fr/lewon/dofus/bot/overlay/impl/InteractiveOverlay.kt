package fr.lewon.dofus.bot.overlay.impl

import fr.lewon.dofus.bot.overlay.AbstractOverlay
import fr.lewon.dofus.bot.overlay.AbstractOverlayPanel
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.swing.JPanel

object InteractiveOverlay : AbstractOverlay() {

    override fun additionalInit() {
        // Nothing
    }

    override fun buildContentPane(): JPanel {
        return InteractiveOverlayPanel(this)
    }

    override fun buildOverlayBounds(): Rectangle {
        return gameInfo.completeBounds
    }

    override fun updateOverlay(gameInfo: GameInfo) {
        super.updateOverlay(gameInfo)
        (contentPane as InteractiveOverlayPanel).updateInteractives(gameInfo)
    }

    private class InteractiveOverlayPanel(overlay: AbstractOverlay) : AbstractOverlayPanel(overlay) {

        private data class InteractiveData(
            val interactiveElement: InteractiveElement,
            val bounds: RectangleAbsolute,
            val gfx: BufferedImage?,
            val clickLocations: List<PointAbsolute>
        )

        var interactiveDataList = emptyList<InteractiveData>()

        fun updateInteractives(gameInfo: GameInfo) {
            interactiveDataList = gameInfo.interactiveElements.filter { it.onCurrentMap }.map { interactiveElement ->
                val bounds = InteractiveUtil.getInteractiveBounds(gameInfo, interactiveElement.elementId)
                val gfx = InteractiveUtil.getInteractiveGfx(gameInfo, interactiveElement.elementId)
                val potentialClickLocations =
                    InteractiveUtil.getInteractivePotentialClickLocations(gameInfo, interactiveElement.elementId)
                InteractiveData(interactiveElement, bounds, gfx, potentialClickLocations)
            }
        }

        override fun onHover(mouseLocation: Point) {
            //Nothing
        }

        override fun drawBackground(g: Graphics) {
            //Nothing
        }

        override fun drawOverlay(g: Graphics) {
            interactiveDataList.sortedByDescending { it.bounds.width * it.bounds.height }.forEach { interactive ->
                val rect = interactive.bounds
                g.color = Color.LIGHT_GRAY
                g.fillRect(rect.x, rect.y, rect.width, rect.height)
                g.color = Color.BLACK
                g.drawString(interactive.interactiveElement.elementId.toString(), rect.x, rect.y)
                g.drawRect(rect.x, rect.y, rect.width, rect.height)
                g.drawImage(interactive.gfx, rect.x, rect.y, rect.width, rect.height, null)
                interactive.clickLocations.forEach { point ->
                    g.drawLine(point.x - 5, point.y, point.x + 5, point.y)
                    g.drawLine(point.x, point.y - 5, point.x, point.y + 5)
                }
            }
        }

    }
}