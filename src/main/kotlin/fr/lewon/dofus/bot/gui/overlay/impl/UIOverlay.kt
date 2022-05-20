package fr.lewon.dofus.bot.gui.overlay.impl

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.core.ui.xml.modele.uixml.Container
import fr.lewon.dofus.bot.gui.overlay.AbstractOverlay
import fr.lewon.dofus.bot.gui.overlay.AbstractOverlayPanel
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.JPanel

object UIOverlay : AbstractOverlay() {

    override fun buildContentPane(): JPanel {
        return UIOverlayPanel(this)
    }

    override fun buildOverlayBounds(): Rectangle {
        return gameInfo.completeBounds
    }

    override fun updateOverlay(gameInfo: GameInfo) {
        super.updateOverlay(gameInfo)
    }

    private class UIOverlayPanel(overlay: AbstractOverlay) : AbstractOverlayPanel(overlay) {

        var uiElement = DofusUIElement.values().first()

        init {
            val comboBox = JComboBox(DofusUIElement.values())
            comboBox.addItemListener {
                if (it.stateChange == ItemEvent.SELECTED) {
                    uiElement = it.item as DofusUIElement
                    val container = uiElement.getDefaultContainer()
                    println("++++")
                    printContainerSizes(container)
                }
            }
            add(comboBox)
        }

        private fun printContainerSizes(container: Container, level: Int = 0) {
            val prefix = "-".repeat(level)
            println("$prefix ${container.name} : POS : (${container.position.x}, ${container.position.y}) / SIZE : (${container.size.x}, ${container.size.y})")
            if (container.name == "gd_zaap") {
                println("----")
                container.anchors.anchorList.forEach { println(it) }
                println("----")
            }
            for (child in container.containers) {
                printContainerSizes(child, level + 1)
            }
        }

        override fun onHover(mouseLocation: Point) {
            //Nothing
        }

        override fun drawBackground(g: Graphics) {
            //Nothing
        }

        override fun drawOverlay(g: Graphics) {
            val container = uiElement.getDefaultContainer()
            val origin = uiElement.getPosition()
            drawContainer(g, container)
        }

        private fun drawContainer(
            g: Graphics,
            container: Container
        ) {
            val position = container.position
            if (container == container.root || container.name.isNotEmpty()) {
                g.color = when (container.name) {
                    "gd_zaap" -> Color.RED
                    "tx_availableKamas" -> Color.BLUE
                    "btn_resetSearch" -> Color.GREEN
                    else -> Color.BLACK
                }
                val positionAbs = ConverterUtil.toPointAbsolute(gameInfo, position)
                val sizeAbs = ConverterUtil.toPointAbsolute(gameInfo, container.size)
                g.drawRect(positionAbs.x, positionAbs.y, sizeAbs.x, sizeAbs.y)
                g.drawString(container.name, positionAbs.x, positionAbs.y)
            }
            for (subContainer in container.containers) {
                drawContainer(g, subContainer)
            }
        }

    }
}