package fr.lewon.dofus.bot.gui.overlay.impl

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.gui.overlay.AbstractMapOverlay
import fr.lewon.dofus.bot.gui.overlay.AbstractMapOverlayPanel
import fr.lewon.dofus.bot.gui.overlay.OverlayUtil
import fr.lewon.dofus.bot.gui.overlay.line.OverlayInfoLine
import fr.lewon.dofus.bot.gui.overlay.line.OverlayTextLine
import fr.lewon.dofus.bot.util.geometry.PointRelative
import java.awt.Color
import java.awt.Graphics

object LOSOverlay : AbstractMapOverlay() {

    override fun additionalInit() {
        // Nothing
    }
    
    override fun buildContentPane(): AbstractMapOverlayPanel {
        return LOSHelperPanel(this)
    }

    private class LOSHelperPanel(mapOverlay: AbstractMapOverlay) : AbstractMapOverlayPanel(mapOverlay) {

        var losOkCells: List<DofusCell> = ArrayList()

        override fun onCellHover() {
            losOkCells = hoveredCell?.let {
                overlay.gameInfo.dofusBoard.cells.filter { c ->
                    c.isAccessible() && overlay.gameInfo.fightBoard.lineOfSight(it, c)
                }
            } ?: emptyList()
        }

        override fun canBeHovered(cell: DofusCell): Boolean {
            return cell.isAccessible()
        }

        override fun getCellColor(cell: DofusCell): Color? {
            val fighter = overlay.gameInfo.fightBoard.getFighter(cell)
            val isCellEnemy = fighter?.let { overlay.gameInfo.fightBoard.isFighterEnemy(it) } == true
            val isCellAlly = fighter?.let { overlay.gameInfo.fightBoard.isFighterEnemy(it) } == false
            return when {
                hoveredCell == cell -> Color.CYAN
                losOkCells.contains(cell) -> Color.BLUE
                isCellEnemy -> Color.RED
                isCellAlly -> Color.GREEN
                else -> Color.LIGHT_GRAY
            }
        }

        override fun drawAdditionalCellContent(g: Graphics, cell: DofusCell) {
            if (!cell.isAccessible()) {
                return
            }
            val fighter = overlay.gameInfo.fightBoard.getFighter(cell)
            val isCellEnemy = fighter?.let { overlay.gameInfo.fightBoard.isFighterEnemy(it) } == true
            val isCellAlly = fighter?.let { overlay.gameInfo.fightBoard.isFighterEnemy(it) } == false
            if (isCellAlly || isCellEnemy) {
                val center = cell.getCenter()
                val halfCellPolygon = OverlayUtil.buildPolygon(
                    overlay.gameInfo,
                    listOf(
                        PointRelative(center.x, center.y - cell.bounds.height),
                        PointRelative(center.x + cell.bounds.width, center.y),
                        PointRelative(center.x, center.y + cell.bounds.height),
                    )
                )
                g.color = if (isCellAlly) Color.GREEN else Color.RED
                g.fillPolygon(halfCellPolygon)
            }
        }

        override fun getCellContentInfo(cell: DofusCell): List<OverlayInfoLine>? {
            return overlay.gameInfo.fightBoard.getFighter(cell)?.let { fighter ->
                listOf(
                    "HP : ${fighter.getCurrentHp()} / ${fighter.maxHp}",
                    "Shield : ${DofusCharacteristics.SHIELD.getValue(fighter)}",
                    "Strength : ${DofusCharacteristics.STRENGTH.getValue(fighter)}",
                    "Agility : ${DofusCharacteristics.AGILITY.getValue(fighter)}",
                    "Intelligence : ${DofusCharacteristics.INTELLIGENCE.getValue(fighter)}",
                    "Chance : ${DofusCharacteristics.CHANCE.getValue(fighter)}",
                    "Power : ${DofusCharacteristics.DAMAGES_BONUS_PERCENT.getValue(fighter)}"
                ).map { OverlayTextLine(it, 14) }
            }
        }
    }
}