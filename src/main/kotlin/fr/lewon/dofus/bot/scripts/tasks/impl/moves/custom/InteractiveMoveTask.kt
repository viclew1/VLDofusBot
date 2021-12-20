package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.manager.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.max
import kotlin.math.min

class InteractiveMoveTask(private val elementId: Int) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
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
                -elementData.origin.y + elementData.size.y * 0.2f
            )
        } else {
            UIPoint(0f, 0f)
        }
        val delta = UIPoint(
            dElementData.x + graphicalElement.pixelOffset.x,
            dElementData.y + graphicalElement.pixelOffset.y - graphicalElement.altitude * 10 - destCellCompleteData.cellData.floor
        )

        val dRelativePoint = ConverterUtil.toPointRelative(delta)
        val destCellId = destCellCompleteData.cellId
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        val destPointRelative = destCell.getCenter().getSum(dRelativePoint)
        destPointRelative.x = max(0.001f, min(destPointRelative.x, 0.99f))
        destPointRelative.y = max(0.001f, min(destPointRelative.y, 0.99f))
        return MoveUtil.processMove(gameInfo, destPointRelative)
    }

    override fun onStarted(): String {
        return "Moving using element [$elementId] ..."
    }
}