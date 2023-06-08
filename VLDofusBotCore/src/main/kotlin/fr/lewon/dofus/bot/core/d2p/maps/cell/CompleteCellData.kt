package fr.lewon.dofus.bot.core.d2p.maps.cell

import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement

class CompleteCellData(val cellId: Int, val cellData: CellData, val graphicalElements: List<GraphicalElement>) {
    fun getGraphicalElement(elementId: Int): GraphicalElement =
        graphicalElements.firstOrNull { it.identifier == elementId }
            ?: error("No graphical element found for element : $elementId")
}
