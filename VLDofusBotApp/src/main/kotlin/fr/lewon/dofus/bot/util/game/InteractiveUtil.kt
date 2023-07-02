package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.EntityGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameMapMovementRequestMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.InteractiveUseRequestMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElementSkill
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Color
import kotlin.math.abs
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

    private val INVALID_SKILL_IDS = listOf(339, 360, 361, 362)

    private val CUSTOM_CLICK_LOCATIONS_BY_INTERACTIVE = mapOf(
        518476 to { bounds: RectangleAbsolute -> // 20 ; -36
            listOf(bounds.getCenter().getSum(PointAbsolute(bounds.width / 3, 0)))
        },
        485282 to { bounds: RectangleAbsolute -> // -1 ; -42
            listOf(bounds.getCenter().getSum(PointAbsolute(bounds.width / 3, bounds.height / 3)))
        }
    )

    fun getElementCellData(gameInfo: GameInfo, interactiveElement: InteractiveElement): CompleteCellData =
        gameInfo.mapData.completeCellDataByCellId.values
            .firstOrNull { it.graphicalElements.map { ge -> ge.identifier }.contains(interactiveElement.elementId) }
            ?: error("No cell data found for element : ${interactiveElement.elementId}")

    fun getInteractiveElement(gameInfo: GameInfo, elementId: Int): InteractiveElement =
        gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
            ?: error("Element not found on map : $elementId")

    fun getInteractiveBounds(gameInfo: GameInfo, elementId: Int): RectangleAbsolute {
        val interactiveElement = getInteractiveElement(gameInfo, elementId)
        val destCellCompleteData = getElementCellData(gameInfo, interactiveElement)
        val graphicalElement = destCellCompleteData.graphicalElements
            .firstOrNull { it.identifier == interactiveElement.elementId }
            ?: error("No graphical element found for element : ${interactiveElement.elementId}")

        val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
        val destCellId = destCellCompleteData.cellId
        val cell = gameInfo.dofusBoard.getCell(destCellId)
        val dToOrigin: UIPoint
        val size: UIPoint
        when {
            elementData is NormalGraphicalElementData && (graphicalElement.altitude < 50 || cell.cellData.floor != 0) -> {
                dToOrigin = elementData.origin
                size = elementData.size
            }
            elementData is EntityGraphicalElementData -> {
                //TODO fetch bones to have real size
                val topLeft = cell.bounds.getTopLeft().toUIPoint()
                val bottomRight = cell.bounds.getBottomRight().toUIPoint()
                size = UIPoint(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
                dToOrigin = UIPoint(size.x / 2f, size.y / 2f)
            }
            else -> {
                val topLeft = cell.bounds.getTopLeft().toUIPoint()
                val bottomRight = cell.bounds.getBottomRight().toUIPoint()
                size = UIPoint(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
                dToOrigin = UIPoint(size.x / 2f, size.y / 2f)
            }
        }
        val altitudeYOffset = if (graphicalElement.altitude < 50) {
            -graphicalElement.altitude * 10
        } else if (cell.cellData.floor != 0) {
            -cell.cellData.floor
        } else 0
        val offset = UIPoint(
            x = graphicalElement.pixelOffset.x,
            y = graphicalElement.pixelOffset.y + altitudeYOffset
        )
        val cellCenter = cell.getCenter().toUIPoint().transpose(offset)
        val minPoint = PointRelative(0.03f, 0.018f).toUIPoint()
        val maxPoint = PointRelative(0.97f, 0.88f).toUIPoint()
        val rawX1 = cellCenter.x - dToOrigin.x
        val rawY1 = cellCenter.y - dToOrigin.y
        val rawX2 = cellCenter.x - dToOrigin.x + size.x
        val rawY2 = cellCenter.y - dToOrigin.y + size.y
        val topLeft = UIPoint(
            x = max(minPoint.x, min(rawX1, maxPoint.x)),
            y = max(minPoint.y, min(rawY1, maxPoint.y))
        ).toPointAbsolute(gameInfo)
        val bottomRight = UIPoint(
            x = max(minPoint.x, min(rawX2, maxPoint.x)),
            y = max(minPoint.y, min(rawY2, maxPoint.y))
        ).toPointAbsolute(gameInfo)
        return RectangleAbsolute.build(topLeft, bottomRight)
    }

    fun getInteractivePotentialClickLocations(gameInfo: GameInfo, elementId: Int): List<PointAbsolute> {
        val bounds = getInteractiveBounds(gameInfo, elementId).let {
            val margin = PointAbsolute(it.width / 10, it.height / 10)
            RectangleAbsolute.build(
                topLeft = it.getTopLeft().getSum(margin),
                bottomRight = it.getBottomRight().getDifference(margin)
            )
        }
        CUSTOM_CLICK_LOCATIONS_BY_INTERACTIVE[elementId]?.let {
            return it(bounds)
        }
        val center = bounds.getCenter()
        val boundsRelative = bounds.toRectangleRelative(gameInfo)
        if (boundsRelative.width < 0.035f && boundsRelative.height < 0.03f) {
            return listOf(center.getSum(PointAbsolute(0, -bounds.height / 3)))
        }
        return listOf(
            center, // Center
            center.getSum(PointAbsolute(-bounds.width / 3, -bounds.height / 3)), // Top left
            center.getSum(PointAbsolute(bounds.width / 3, -bounds.height / 3)), // Top right
            center.getSum(PointAbsolute(-bounds.width / 3, bounds.height / 3)), // Bottom left
            center.getSum(PointAbsolute(bounds.width / 3, bounds.height / 3)), // Bottom right
            center.getSum(PointAbsolute(0, -bounds.height / 3)), // Center top
            center.getSum(PointAbsolute(0, bounds.height / 3)), // Center bottom
            center.getSum(PointAbsolute(-bounds.width / 3, 0)), // Center left
            center.getSum(PointAbsolute(bounds.width / 3, 0)), // Center right
        )
    }

    private fun getElementClickPosition(gameInfo: GameInfo, elementId: Int): PointAbsolute {
        val entitiesCellIds = gameInfo.entityPositionsOnMapByEntityId.values
        val interactiveElementsCellIds = gameInfo.interactiveElements
            .filter { it.elementId != elementId }
            .map { getElementCellData(gameInfo, it).cellId }
        val toAvoidAbsoluteLocations = entitiesCellIds.plus(interactiveElementsCellIds)
            .map { cellId -> gameInfo.dofusBoard.getCell(cellId) }
            .map { cell -> cell.getCenter().toPointAbsolute(gameInfo) }
        return getInteractivePotentialClickLocations(gameInfo, elementId).maxBy { clickLocation ->
            toAvoidAbsoluteLocations.minOfOrNull { entityLocation ->
                abs(clickLocation.x - entityLocation.x) + abs(clickLocation.y - entityLocation.y)
            } ?: Int.MAX_VALUE
        }
    }

    fun useInteractive(gameInfo: GameInfo, elementId: Int, skillId: Int) {
        val element = gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
            ?: error("Element not found on current map : $elementId")
        val skills = element.enabledSkills.filter { !INVALID_SKILL_IDS.contains(it.skillId) }
        val elementClickLocation = getElementClickPosition(gameInfo, elementId)
        val skillIndex = skills.map { it.skillId }.indexOf(skillId)
        if (skillIndex < 0) {
            error("No skill available on interactive : $skillId on element : $elementId")
        }

        gameInfo.eventStore.clear()
        RetryUtil.tryUntilSuccess(
            { doUseInteractive(gameInfo, elementClickLocation.toPointRelative(gameInfo), skills, skillIndex) },
            { waitUntilInteractiveUseRequestSent(gameInfo) },
            4
        ) ?: error("No interactive used")
    }

    private fun waitUntilInteractiveUseRequestSent(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(1500) {
        gameInfo.eventStore.getLastEvent(InteractiveUseRequestMessage::class.java) != null
                || gameInfo.eventStore.getLastEvent(GameMapMovementRequestMessage::class.java) != null
    }

    private fun doUseInteractive(
        gameInfo: GameInfo,
        elementClickLocation: PointRelative,
        skills: List<InteractiveElementSkill>,
        skillIndex: Int
    ) {
        if (skills.filter { !INVALID_SKILL_IDS.contains(it.skillId) }.size > 1) {
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
        if (!WaitUtil.waitUntil(3000) { isOptionFound(gameInfo, interactiveLocation, optionHeaderRect) }) {
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
        val dRelativePoint = delta.toPointRelative()
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
        val dFloor = UIPoint(y = floor.toFloat()).toPointRelative()
        return PointRelative(
            cellCenter.x + dxMultiplier * cellBounds.width * 0.8f,
            cellCenter.y - dFloor.y
        )
    }

}