package fr.lewon.dofus.bot.gui.vldb.overlay.impl

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.core.ui.xml.containers.Container
import fr.lewon.dofus.bot.gui.vldb.overlay.AbstractOverlay
import fr.lewon.dofus.bot.gui.vldb.overlay.AbstractOverlayPanel
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.JPanel

object UIOverlay : AbstractOverlay() {

    override fun additionalInit() {
        // Nothing
    }

    override fun buildContentPane(): JPanel {
        return UIOverlayPanel(this)
    }

    override fun buildOverlayBounds(): Rectangle {
        return gameInfo.completeBounds
    }

    override fun updateOverlay(gameInfo: GameInfo) {
        super.updateOverlay(gameInfo)
        (contentPane as UIOverlayPanel).updateContainer()
    }

    private class UIOverlayPanel(overlay: AbstractOverlay) : AbstractOverlayPanel(overlay) {

        private val toDrawRectangles = ArrayList<Pair<Container, Rectangle>>()
        private val uiElementComboBox = JComboBox(DofusUIElement.values())
        private var hoveredContainer: Container? = null

        init {
            uiElementComboBox.addItemListener {
                if (it.stateChange == ItemEvent.SELECTED) {
                    updateContainer()
                }
            }
            add(uiElementComboBox)
        }

        fun updateContainer() {
            val container = (uiElementComboBox.selectedItem as DofusUIElement).getContainer()
            toDrawRectangles.clear()
            addRectangles(container)
        }

        private fun addRectangles(container: Container) {
            val rectAbs = ConverterUtil.toRectangleAbsolute(container.bounds)
            toDrawRectangles.add(container to Rectangle(rectAbs.x, rectAbs.y, rectAbs.width, rectAbs.height))
            for (subContainer in container.children) {
                addRectangles(subContainer)
            }
        }

        override fun onHover(mouseLocation: Point) {
            hoveredContainer = getContainerAtLocation(mouseLocation)
        }

        private fun getContainerAtLocation(location: Point): Container? {
            return toDrawRectangles.filter { it.second.contains(location) }
                .minByOrNull { it.second.width * it.second.height }
                ?.first
        }

        private fun getContainerLevel(container: Container): Int {
            var level = 1
            if (container != container.root) {
                level += getContainerLevel(container.parentContainer)
            }
            return level
        }

        override fun drawBackground(g: Graphics) {
            //Nothing
        }

        override fun drawOverlay(g: Graphics) {
            toDrawRectangles.forEach {
                val container = it.first
                val rect = it.second
                if (container == hoveredContainer) {
                    g.color = Color.LIGHT_GRAY
                    g.fillRect(rect.x, rect.y, rect.width, rect.height)
                    g.color = Color.WHITE
                    g.drawString(container.name, rect.x, rect.y)
                    g.color = Color.BLACK
                    g.drawRect(rect.x, rect.y, rect.width, rect.height)
                } else if (getContainerLevel(container) == 1) {
                    g.color = Color.DARK_GRAY
                    g.fillRect(rect.x, rect.y, rect.width, rect.height)
                    g.color = Color.BLACK
                    g.drawRect(rect.x, rect.y, rect.width, rect.height)
                }
            }
        }

    }
}