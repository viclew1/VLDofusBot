package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.manager.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.manager.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.max
import kotlin.math.min

object InteractiveUtil {

    fun getElementClickPosition(gameInfo: GameInfo, elementId: Int): PointRelative {
        val interactiveElement = gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
            ?: error("Element not found on map : $elementId")

        val destCellCompleteData = gameInfo.completeCellDataByCellId.values
            .firstOrNull { it.graphicalElements.map { ge -> ge.identifier }.contains(interactiveElement.elementId) }
            ?: error("No cell data found for element : $elementId")

        val graphicalElement = destCellCompleteData.graphicalElements
            .firstOrNull { it.identifier == interactiveElement.elementId }
            ?: error("No graphical element found for element : $elementId")

        val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
        val dElementData = if (elementData is NormalGraphicalElementData) {
            UIPoint(
                -elementData.origin.x + elementData.size.x / 2f,
                -elementData.origin.y + elementData.size.y * 0.25f
            )
        } else {
            UIPoint(0f, 0f)
        }
        val delta = UIPoint(
            dElementData.x + graphicalElement.pixelOffset.x,
            dElementData.y + graphicalElement.pixelOffset.y - graphicalElement.altitude * 10
        )
        val dRelativePoint = ConverterUtil.toPointRelative(delta)
        val destCellId = destCellCompleteData.cellId
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        val destPointRelative = destCell.getCenter().getSum(dRelativePoint)
        destPointRelative.x = max(0.001f, min(destPointRelative.x, 0.99f))
        destPointRelative.y = max(0.001f, min(destPointRelative.y, 0.99f))
        return destPointRelative
    }

}