package fr.lewon.dofus.bot.gui.overlay.impl

import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.gfx.D2PGfxAdapter
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.gui.overlay.AbstractMapOverlay
import fr.lewon.dofus.bot.gui.overlay.AbstractMapOverlayPanel
import fr.lewon.dofus.bot.gui.overlay.line.OverlayImageLine
import fr.lewon.dofus.bot.gui.overlay.line.OverlayInfoLine
import fr.lewon.dofus.bot.gui.overlay.line.OverlayTextLine
import fr.lewon.dofus.bot.gui.util.ImageUtil
import java.awt.Color
import java.awt.Graphics

object GfxOverlay : AbstractMapOverlay() {

    override fun additionalInit() {
        opacity = 0.9f
    }

    override fun buildContentPane(): AbstractMapOverlayPanel {
        return GfxOverlayPanel(this)
    }

    private class GfxOverlayPanel(mapOverlay: AbstractMapOverlay) : AbstractMapOverlayPanel(mapOverlay) {

        override fun onCellHover() {
            //Nothing
        }

        override fun canBeHovered(cell: DofusCell): Boolean {
            return true
        }

        override fun getCellColor(cell: DofusCell): Color? {
            return Color.LIGHT_GRAY
        }

        override fun drawAdditionalCellContent(g: Graphics, cell: DofusCell) {
            //Nothing
        }

        override fun getCellContentInfo(cell: DofusCell): List<OverlayInfoLine>? {
            val graphicalElements = gameInfo.completeCellDataByCellId[cell.cellId]?.graphicalElements
                ?: return null
            val lines = ArrayList<OverlayInfoLine>()
            lines.add(OverlayTextLine("Cell ID : ${cell.cellId}", 14))
            lines.add(OverlayTextLine("------------", 8))
            for (graphicalElement in graphicalElements) {
                lines.add(OverlayTextLine("Graphical elem offset : ${graphicalElement.pixelOffset}", 14))
                val element = D2PElementsAdapter.getElement(graphicalElement.elementId)
                if (element is NormalGraphicalElementData) {
                    val gfxId = element.gfxId
                    val gfxImageData = D2PGfxAdapter.getGfxImageDataById(gfxId.toDouble())
                    lines.add(OverlayTextLine("GFX ID : $gfxId", 14))
                    val image = ImageUtil.getScaledImageKeepHeight(gfxImageData, 60)
                    lines.add(OverlayImageLine(image))
                }
            }
            return lines
        }
    }

}