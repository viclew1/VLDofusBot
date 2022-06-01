package fr.lewon.dofus.bot.gui.vldb.overlay.impl

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.gui.vldb.overlay.AbstractMapOverlay
import fr.lewon.dofus.bot.gui.vldb.overlay.AbstractMapOverlayPanel
import fr.lewon.dofus.bot.gui.vldb.overlay.line.OverlayInfoLine
import fr.lewon.dofus.bot.gui.vldb.overlay.line.OverlayTextLine
import java.awt.Color
import java.awt.Graphics

object BreedingOverlay : AbstractMapOverlay() {

    private val PADDOCK_ITEM_CELL_IDS = listOf(356, 342, 329, 315, 314)

    override fun additionalInit() {
        // Nothing
    }

    override fun buildContentPane(): AbstractMapOverlayPanel {
        return BreedingHelperPanel(this)
    }

    private class BreedingHelperPanel(mapOverlay: AbstractMapOverlay) : AbstractMapOverlayPanel(mapOverlay) {

        override fun onCellHover() {
            //Nothing
        }

        override fun canBeHovered(cell: DofusCell): Boolean {
            return true
        }

        override fun getCellColor(cell: DofusCell): Color? {
            return when {
                overlay.gameInfo.paddockItemByCell[cell.cellId] != null -> Color.YELLOW
                hoveredCell == cell -> Color.CYAN
                else -> Color.LIGHT_GRAY
            }
        }

        override fun drawAdditionalCellContent(g: Graphics, cell: DofusCell) {
            //Nothing
        }

        override fun getCellContentInfo(cell: DofusCell): List<OverlayInfoLine>? {
            return overlay.gameInfo.paddockItemByCell[cell.cellId]?.let { paddockItem ->
                listOf(
                    "Cell ID : ${paddockItem.cellId}",
                    "Durability : ${paddockItem.durability.durability}",
                    "Max durability : ${paddockItem.durability.durabilityMax}",
                ).map { OverlayTextLine(it, 14) }
            }
        }
    }

}