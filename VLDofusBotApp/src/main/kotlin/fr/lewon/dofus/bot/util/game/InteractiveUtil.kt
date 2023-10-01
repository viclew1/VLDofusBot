package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementData
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.EntityGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.gfx.D2PWorldGfxAdapter
import fr.lewon.dofus.bot.core.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement
import fr.lewon.dofus.bot.core.d2p.sprite.D2PBonesSpriteAdapter
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.UIRectangle
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameCautiousMapMovementRequestMessage
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
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
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
        val bounds = getRawInteractiveBounds(gameInfo, destCellCompleteData, elementData, graphicalElement)
        val boundsCropDelta = getBoundsCropDelta(bounds)
        return cropBounds(bounds, boundsCropDelta).toRectangleAbsolute(gameInfo)
    }

    fun getInteractiveGfx(gameInfo: GameInfo, elementId: Int): BufferedImage? {
        val interactiveElement = getInteractiveElement(gameInfo, elementId)
        val destCellCompleteData = getElementCellData(gameInfo, interactiveElement)
        val graphicalElement = destCellCompleteData.graphicalElements
            .firstOrNull { it.identifier == interactiveElement.elementId }
            ?: error("No graphical element found for element : ${interactiveElement.elementId}")

        val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
        return when (elementData) {
            is EntityGraphicalElementData -> {
                val boneId = elementData.entityLook.substring(1, elementData.entityLook.length - 1).toInt()
                D2PBonesSpriteAdapter.getBoneSprite(boneId.toDouble())?.getImage(elementData.horizontalSymmetry)
            }
            else -> null
        }
    }

    private fun cropBounds(bounds: UIRectangle, boundsCropDelta: UIRectangleDelta): UIRectangle {
        return UIRectangle(
            position = bounds.position.transpose(x = boundsCropDelta.leftDelta, y = boundsCropDelta.topDelta),
            size = bounds.size.transpose(
                x = -boundsCropDelta.rightDelta - boundsCropDelta.leftDelta,
                y = -boundsCropDelta.bottomDelta - boundsCropDelta.topDelta
            ).let {
                UIPoint(x = max(0f, it.x), y = max(0f, it.y))
            }
        )
    }

    private data class UIRectangleDelta(
        val leftDelta: Float,
        val rightDelta: Float,
        val topDelta: Float,
        val bottomDelta: Float,
    )

    private fun getBoundsCropDelta(bounds: UIRectangle): UIRectangleDelta {
        val minPoint = PointRelative(0.03f, 0.018f).toUIPoint()
        val maxPoint = PointRelative(0.97f, 0.88f).toUIPoint()
        return UIRectangleDelta(
            leftDelta = max(0f, minPoint.x - bounds.position.x),
            rightDelta = max(0f, bounds.position.x + bounds.size.x - maxPoint.x),
            topDelta = max(0f, minPoint.y - bounds.position.y),
            bottomDelta = max(0f, bounds.position.y + bounds.size.y - maxPoint.y),
        )
    }

    private fun getRawInteractiveBounds(
        gameInfo: GameInfo,
        destCellCompleteData: CompleteCellData,
        elementData: GraphicalElementData,
        graphicalElement: GraphicalElement,
    ): UIRectangle {
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
                val boneId = elementData.entityLook.substring(1, elementData.entityLook.length - 1).toInt()
                /* TODO: implement EntityLookParser / TiphonEntityLook?
                 *       Maybe not useful, we only encounter the simple `{boneId}` format */
                val boneSprite = D2PBonesSpriteAdapter.getBoneSprite(boneId.toDouble())
                if (boneSprite != null) {
                    val rect = boneSprite.getBounds(elementData.horizontalSymmetry)
                    size = rect.size
                    dToOrigin = rect.position.invert()
                } else {
                    println("Bone (#$boneId) sprite not found, defaulting to cell bounds")
                    val topLeft = cell.bounds.getTopLeft().toUIPoint()
                    val bottomRight = cell.bounds.getBottomRight().toUIPoint()
                    size = UIPoint(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
                    dToOrigin = UIPoint(size.x / 2f, size.y / 2f)
                }
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
        val x = cellCenter.x - dToOrigin.x
        val y = cellCenter.y - dToOrigin.y
        return UIRectangle(UIPoint(x, y), size)
    }

    fun getInteractivePotentialClickLocations(gameInfo: GameInfo, elementId: Int): List<PointAbsolute> {
        val interactiveElement = getInteractiveElement(gameInfo, elementId)
        val destCellCompleteData = getElementCellData(gameInfo, interactiveElement)
        val graphicalElement = destCellCompleteData.graphicalElements
            .firstOrNull { it.identifier == interactiveElement.elementId }
            ?: error("No graphical element found for element : ${interactiveElement.elementId}")

        val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
        val rawBounds = getRawInteractiveBounds(gameInfo, destCellCompleteData, elementData, graphicalElement)
        val boundsCropDelta = getBoundsCropDelta(rawBounds)
        val realBounds = cropBounds(rawBounds, boundsCropDelta)
        getCustomClickLocations(elementId, realBounds.toRectangleAbsolute(gameInfo))?.let {
            return it
        }
        val gfx = when (elementData) {
            is NormalGraphicalElementData -> {
                val gfxByteArray = D2PWorldGfxAdapter.getWorldGfxImageData(elementData.gfxId.toDouble())
                ImageIO.read(ByteArrayInputStream(gfxByteArray))
            }
            is EntityGraphicalElementData -> {
                val boneId = elementData.entityLook.substring(1, elementData.entityLook.length - 1).toInt()
                val boneSprite = D2PBonesSpriteAdapter.getBoneSprite(boneId.toDouble())
                boneSprite?.getImage(elementData.horizontalSymmetry)
            }
            else -> null
        }
        val horizontalSymmetry = if (elementData is NormalGraphicalElementData) {
            elementData.horizontalSymmetry
        } else false
        val size = realBounds.size
        val clickDeltaLocations = gfx?.let {
            GfxPointToCheck.getPoints(boundsCropDelta).filter {
                val x = it.xPoint.getPoint(size.x) + boundsCropDelta.leftDelta
                val y = it.yPoint.getPoint(size.y) + boundsCropDelta.topDelta
                val realX = if (horizontalSymmetry) gfx.data.width - x else x
                if (realX.toInt() <= gfx.width && y.toInt() <= gfx.height) {
                    gfx.getRGB(realX.toInt(), y.toInt()) != 0
                } else false
            }.takeIf { it.isNotEmpty() }
        } ?: listOf(GfxPointToCheck.TopCenter)
        return clickDeltaLocations.map {
            val x = it.xPoint.getPoint(size.x)
            val y = it.yPoint.getPoint(size.y)
            realBounds.position.transpose(UIPoint(x, y)).toPointAbsolute(gameInfo)
        }
    }

    private fun getCustomClickLocations(elementId: Int, bounds: RectangleAbsolute) = when (elementId) {
        518476, // 20 ; -36
        -> listOf(bounds.getCenter().getSum(PointAbsolute(bounds.width / 3, 0)))
        485282, // -1 ; -42
        522746, // -28 ; 35
        484082, // -30; -11
        -> listOf(bounds.getCenter().getSum(PointAbsolute(bounds.width / 3, bounds.height / 3)))
        523968, // -24 ; 39
        523592, // -23 ; 39
        -> listOf(bounds.getCenter())
        515577, // 5 ; -17
        483927, // 22 ; 22
        -> listOf(bounds.getCenter().getSum(PointAbsolute(bounds.width / 3, -bounds.height / 3)))
        521652, // -36 ; -60
        523654, // -33 ; -58
        -> listOf(bounds.getCenter().getSum(PointAbsolute(-bounds.width / 3, bounds.height / 3)))
        522812, // -23 ; 38
        510178, // -22 ; 23
        -> listOf(bounds.getTopRight().getSum(PointAbsolute(-2, 2)))
        else -> null
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
        gameInfo.eventStore.clear()
        RetryUtil.tryUntilSuccess(
            {
                val element = gameInfo.interactiveElements.firstOrNull { it.elementId == elementId }
                    ?: error("Element not found on current map : $elementId")
                val skills = element.enabledSkills.filter { !INVALID_SKILL_IDS.contains(it.skillId) }
                val elementClickLocation = getElementClickPosition(gameInfo, elementId)
                val skillIndex = skills.map { it.skillId }.indexOf(skillId)
                if (skillIndex < 0) {
                    error("No skill available on interactive : $skillId on element : $elementId")
                }
                doUseInteractive(gameInfo, elementClickLocation.toPointRelative(gameInfo), skills, skillIndex)
            },
            { waitUntilInteractiveUseRequestSent(gameInfo) },
            4
        ) ?: error("No interactive used")
    }

    private fun waitUntilInteractiveUseRequestSent(gameInfo: GameInfo): Boolean = WaitUtil.waitUntil(500) {
        gameInfo.eventStore.getLastEvent(InteractiveUseRequestMessage::class.java) != null
            || gameInfo.eventStore.getLastEvent(GameMapMovementRequestMessage::class.java) != null
            || gameInfo.eventStore.getLastEvent(GameCautiousMapMovementRequestMessage::class.java) != null
    }

    private fun doUseInteractive(
        gameInfo: GameInfo,
        elementClickLocation: PointRelative,
        skills: List<InteractiveElementSkill>,
        skillIndex: Int,
    ) {
        if (skills.filter { !INVALID_SKILL_IDS.contains(it.skillId) }.size > 1) {
            if (!clickButtonWithOptions(gameInfo, elementClickLocation, skillIndex)) {
                error("Couldn't use interactive")
            }
        } else {
            MouseUtil.leftClick(gameInfo, elementClickLocation)
        }
    }

    fun clickButtonWithOptions(
        gameInfo: GameInfo,
        buttonLocation: PointRelative,
        optionIndex: Int,
        optionListHasHeader: Boolean = true
    ): Boolean = RetryUtil.tryUntilSuccess(
        {
            doClickButtonWithOptions(
                gameInfo,
                buttonLocation,
                if (optionListHasHeader) optionIndex else optionIndex - 1
            )
        },
        10
    )

    private fun doClickButtonWithOptions(
        gameInfo: GameInfo,
        buttonLocation: PointRelative,
        optionIndex: Int,
    ): Boolean {
        val optionHeaderRect = REF_HEADER_RECT.getTranslation(REF_INTERACTIVE_LOCATION.opposite())
            .getTranslation(buttonLocation)
        MouseUtil.leftClick(gameInfo, buttonLocation)
        if (!WaitUtil.waitUntil(3000) { isOptionFound(gameInfo, buttonLocation, optionHeaderRect) }) {
            return false
        }
        val firstOptionLoc = REF_FIRST_OPTION_LOCATION.getDifference(REF_INTERACTIVE_LOCATION)
            .getSum(buttonLocation)
        MouseUtil.leftClick(gameInfo, firstOptionLoc.getSum(PointRelative(y = optionIndex.toFloat() * DELTA_OPTION)))
        return true
    }

    private fun isOptionFound(
        gameInfo: GameInfo,
        elementClickLocation: PointRelative,
        optionHeaderRect: RectangleRelative,
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

    private enum class GfxPointToCheck(val xPoint: UIXPoint, val yPoint: UIYPoint) {
        TopLeft(UIXPoint.Left, UIYPoint.Top),
        TopCenter(UIXPoint.Center, UIYPoint.Top),
        TopRight(UIXPoint.Right, UIYPoint.Top),
        CenterLeft(UIXPoint.Left, UIYPoint.Center),
        Center(UIXPoint.Center, UIYPoint.Center),
        CenterRight(UIXPoint.Right, UIYPoint.Center),
        //BottomLeft(UIXPoint.Left, UIYPoint.Bottom),
        //BottomCenter(UIXPoint.Center, UIYPoint.Bottom),
        //BottomRight(UIXPoint.Right, UIYPoint.Bottom)
        ;

        companion object {

            fun getPoints(boundsCropDelta: UIRectangleDelta): List<GfxPointToCheck> {
                val points = entries.toMutableList()
                if (boundsCropDelta.leftDelta > 0) {
                    points.removeIf { it.xPoint == UIXPoint.Right }
                }
                if (boundsCropDelta.rightDelta > 0) {
                    points.removeIf { it.xPoint == UIXPoint.Left }
                }
                if (boundsCropDelta.topDelta > 0) {
                    points.removeIf { it.yPoint == UIYPoint.Bottom }
                }
                if (boundsCropDelta.bottomDelta > 0) {
                    points.removeIf { it.yPoint == UIYPoint.Top }
                }
                return points
            }
        }
    }

    private enum class UIXPoint(val getPoint: (width: Float) -> Float) {
        Left({ width -> width / 5 }),
        Center({ width -> width / 2 }),
        Right({ width -> width * 4 / 5 })
    }

    private enum class UIYPoint(val getPoint: (height: Float) -> Float) {
        Top({ height -> height / 5 }),
        Center({ height -> height / 2 }),
        Bottom({ height -> height * 4 / 5 })
    }
}