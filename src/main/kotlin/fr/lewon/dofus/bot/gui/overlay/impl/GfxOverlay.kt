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
            val gfxIds = gameInfo.completeCellDataByCellId[cell.cellId]?.graphicalElements
                ?.map { D2PElementsAdapter.getElement(it.elementId) }
                ?.filterIsInstance<NormalGraphicalElementData>()
                ?.map { it.gfxId }?.distinct()
                ?: return null
            val lines = ArrayList<OverlayInfoLine>()
            for (gfxId in gfxIds) {
                lines.add(OverlayTextLine("Cell ID : ${cell.cellId} / GFX ID : $gfxId", 14))
                val image = ImageUtil.getScaledImageKeepHeight(D2PGfxAdapter.getGfxImageDataById(gfxId.toDouble()), 30)
                lines.add(OverlayImageLine(image))
            }
            return lines
        }
    }

}