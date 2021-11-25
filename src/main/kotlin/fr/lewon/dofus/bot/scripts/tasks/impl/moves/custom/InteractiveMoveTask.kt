package fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.manager.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.manager.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class InteractiveMoveTask(private val elementId: Int) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val interactiveElement = gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
        if (interactiveElement == null) {
            VldbLogger.info("Element not found on map : $elementId", logItem)
            return false
        }

        val destCellCompleteData = gameInfo.completeCellDataByCellId.values
            .firstOrNull { it.graphicalElements.map { ge -> ge.identifier }.contains(interactiveElement.elementId) }
        if (destCellCompleteData == null) {
            VldbLogger.info("No cell data found for element : $elementId", logItem)
            return false
        }

        val graphicalElement = destCellCompleteData.graphicalElements
            .firstOrNull { it.identifier == interactiveElement.elementId }
        if (graphicalElement == null) {
            VldbLogger.info("No graphical element found for element : $elementId", logItem)
            return false
        }

        val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
        val dUIPoint = if (elementData is NormalGraphicalElementData) {
            println(destCellCompleteData.cellId)
            println(elementData.height)
            println(elementData.horizontalSymmetry)
            println(elementData.origin)
            println(elementData.size)
            val symmetryMultiplier = if (elementData.horizontalSymmetry) -1 else 1
            UIPoint(
                -elementData.origin.x + elementData.size.x / 2f,
                -elementData.origin.y + elementData.size.y / 2f
            )
        } else {
            UIPoint(0f, 0f)
        }
        val dRelativePoint = ConverterUtil.toPointRelative(dUIPoint)
        val destCellId = destCellCompleteData.cellId
        val destCell = gameInfo.dofusBoard.getCell(destCellId)
        return MoveUtil.processMove(gameInfo, destCell.getCenter(), cancellationToken)
    }

    override fun onStarted(): String {
        return "Moving using element [$elementId] ..."
    }
}