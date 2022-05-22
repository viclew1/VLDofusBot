package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

object InteractiveUtil {

    private val REF_INTERACTIVE_LOCATION = PointRelative(0.5502451f, 0.44257274f)
    private val REF_HEADER_RECT = RectangleRelative.build(
        REF_INTERACTIVE_LOCATION, PointRelative(0.560049f, 0.47473204f)
    )
    private val REF_FIRST_OPTION_LOCATION = PointRelative(0.560049f, 0.47473204f)
    private val REF_TENTH_OPTION_LOCATION = PointRelative(0.560049f, 0.6707504f)

    private val DELTA_OPTION = (REF_TENTH_OPTION_LOCATION.y - REF_FIRST_OPTION_LOCATION.y) / 9f

    private val OPTION_HEADER_MIN_COLOR = Color(65, 60, 48)
    private val OPTION_HEADER_MAX_COLOR = Color(73, 68, 56)

    private val SKILL_SIGN_IDS = listOf(360, 361, 362)

    private fun getElementClickPosition(gameInfo: GameInfo, elementId: Int): PointRelative {
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
        destPointRelative.x = max(0.03f, min(destPointRelative.x, 0.97f))
        destPointRelative.y = max(0.018f, min(destPointRelative.y, 0.88f))
        return destPointRelative
    }

    fun useInteractive(gameInfo: GameInfo, elementId: Int, skillId: Int) {
        val element = gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
            ?: error("Element not found on current map : $elementId")
        val skills = element.enabledSkills.filter { !SKILL_SIGN_IDS.contains(it.skillId) }
        val elementClickLocation = getElementClickPosition(gameInfo, elementId)
        val skillIndex = skills.map { it.skillId }.indexOf(skillId)
        if (skillIndex < 0) {
            error("No skill available on interactive : $skillId on element : $elementId")
        }

        if (skills.filter { !SKILL_SIGN_IDS.contains(it.skillId) }.size > 1) {
            if (!RetryUtil.tryUntilSuccess({ clickOption(gameInfo, elementClickLocation, skillIndex) }, 10)) {
                error("Couldn't use interactive")
            }
        } else {
            MouseUtil.leftClick(gameInfo, elementClickLocation)
        }
    }

    private fun clickOption(
        gameInfo: GameInfo,
        interactiveLocation: PointRelative,
        skillIndex: Int
    ): Boolean {
        val optionHeaderRect = REF_HEADER_RECT.getTranslation(REF_INTERACTIVE_LOCATION.opposite())
            .getTranslation(interactiveLocation)
        MouseUtil.leftClick(gameInfo, interactiveLocation)
        if (!WaitUtil.waitUntil({ isOptionFound(gameInfo, interactiveLocation, optionHeaderRect) }, 3000)) {
            return false
        }
        val firstOptionLoc = REF_FIRST_OPTION_LOCATION.getDifference(REF_INTERACTIVE_LOCATION)
            .getSum(interactiveLocation)
        MouseUtil.leftClick(gameInfo, firstOptionLoc.getSum(PointRelative(y = skillIndex.toFloat() * DELTA_OPTION)))
        return true
    }

    private fun isOptionFound(
        gameInfo: GameInfo,
        elementClickLocation: PointRelative,
        optionHeaderRect: RectangleRelative
    ): Boolean {
        MouseUtil.move(gameInfo, elementClickLocation)
        return ScreenUtil.colorCount(
            gameInfo,
            optionHeaderRect,
            OPTION_HEADER_MIN_COLOR,
            OPTION_HEADER_MAX_COLOR,
        ) > 0
    }

    fun getNpcClickPosition(gameInfo: GameInfo, npcId: Int): PointRelative {
        val npcEntityId = gameInfo.entityIdByNpcId[npcId] ?: error("NPC $npcId not on current map")
        val npcCellId = gameInfo.entityPositionsOnMapByEntityId[npcEntityId] ?: error("entity $npcEntityId not found")
        val cell = gameInfo.dofusBoard.getCell(npcCellId)
        val delta = UIPoint(0f, -cell.cellData.floor.toFloat())
        val dRelativePoint = ConverterUtil.toPointRelative(delta)
        val destPointRelative = cell.getCenter().getSum(dRelativePoint)
        destPointRelative.x = max(0.001f, min(destPointRelative.x, 0.99f))
        destPointRelative.y = max(0.001f, min(destPointRelative.y, 0.99f))
        return destPointRelative
    }

    fun getCellClickPosition(gameInfo: GameInfo, cellId: Int, avoidCenter: Boolean = true): PointRelative {
        val cell = gameInfo.dofusBoard.getCell(cellId)
        val cellBounds = cell.bounds
        val cellCenter = cellBounds.getCenter()

        val floor = cell.cellData.floor
        val dxMultiplier = if (floor != 0 || !avoidCenter) 0 else if (cellCenter.x > 0.5) 1 else -1
        val dFloor = ConverterUtil.toPointRelative(UIPoint(y = floor.toFloat()))
        return PointRelative(
            cellCenter.x + dxMultiplier * cellBounds.width * 0.8f,
            cellCenter.y - dFloor.y
        )
    }

}