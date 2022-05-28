package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.core.ui.UIBounds
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.UIRectangle
import fr.lewon.dofus.bot.gui.overlay.impl.UIOverlay
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.network.GameInfo
import kotlin.math.roundToInt

object ConverterUtil {

    private const val PRECISION = 10000.0f

    fun toPointAbsolute(gameInfo: GameInfo, point: UIPoint): PointAbsolute {
        return toPointAbsolute(gameInfo, toPointRelative(point))
    }

    fun toPointRelative(point: UIPoint): PointRelative {
        return PointRelative(
            point.x / UIBounds.TOTAL_WIDTH,
            point.y / UIBounds.TOTAL_HEIGHT
        )
    }

    fun toUIPoint(point: PointRelative): UIPoint {
        return UIPoint(
            point.x * UIBounds.TOTAL_WIDTH,
            point.y * UIBounds.TOTAL_HEIGHT
        )
    }

    fun toUIPoint(gameInfo: GameInfo, point: PointAbsolute): UIPoint {
        return toUIPoint(toPointRelative(gameInfo, point))
    }


    fun toPointRelative(gameInfo: GameInfo, point: PointAbsolute): PointRelative {
        val x = (point.x - gameInfo.gameBounds.x).toFloat() / gameInfo.gameBounds.width.toFloat()
        val y = (point.y - gameInfo.gameBounds.y).toFloat() / gameInfo.gameBounds.height.toFloat()
        return PointRelative((x * PRECISION) / PRECISION, (y * PRECISION) / PRECISION)
    }

    fun toPointAbsolute(gameInfo: GameInfo, point: PointRelative): PointAbsolute {
        val x = point.x * gameInfo.gameBounds.width + gameInfo.gameBounds.x
        val y = point.y * gameInfo.gameBounds.height + gameInfo.gameBounds.y
        return PointAbsolute(x.roundToInt(), y.roundToInt())
    }

    fun toRectangleRelative(gameInfo: GameInfo, rect: RectangleAbsolute): RectangleRelative {
        val x1 = (rect.x - gameInfo.gameBounds.x).toFloat() / gameInfo.gameBounds.width.toFloat()
        val y1 = (rect.y - gameInfo.gameBounds.y).toFloat() / gameInfo.gameBounds.height.toFloat()
        val x2 = (rect.x + rect.width - gameInfo.gameBounds.x).toFloat() / gameInfo.gameBounds.width.toFloat()
        val y2 = (rect.y + rect.height - gameInfo.gameBounds.y).toFloat() / gameInfo.gameBounds.height.toFloat()
        return RectangleRelative(
            (x1 * PRECISION) / PRECISION,
            (y1 * PRECISION) / PRECISION,
            ((x2 - x1) * PRECISION) / PRECISION,
            ((y2 - y1) * PRECISION) / PRECISION
        )
    }

    fun toRectangleAbsolute(gameInfo: GameInfo, rect: RectangleRelative): RectangleAbsolute {
        val x1 = rect.x * gameInfo.gameBounds.width + gameInfo.gameBounds.x
        val y1 = rect.y * gameInfo.gameBounds.height + gameInfo.gameBounds.y
        val x2 = (rect.x + rect.width) * gameInfo.gameBounds.width + gameInfo.gameBounds.x
        val y2 = (rect.y + rect.height) * gameInfo.gameBounds.height + gameInfo.gameBounds.y
        return RectangleAbsolute(x1.roundToInt(), y1.roundToInt(), (x2 - x1).roundToInt(), (y2 - y1).roundToInt())
    }

    fun toRectangleRelative(rect: UIRectangle): RectangleRelative {
        val topLeftPosition = rect.position
        val bottomRightPosition = rect.position.transpose(rect.size)
        return RectangleRelative.build(toPointRelative(topLeftPosition), toPointRelative(bottomRightPosition))
    }

    fun toRectangleAbsolute(rect: UIRectangle): RectangleAbsolute {
        val topLeftAbs = toPointAbsolute(UIOverlay.gameInfo, rect.position)
        val bottomRightUIPoint = UIPoint(rect.position.x + rect.size.x, rect.position.y + rect.size.y)
        val bottomRightAbs = toPointAbsolute(UIOverlay.gameInfo, bottomRightUIPoint)
        val sizeAbs = PointAbsolute(bottomRightAbs.x - topLeftAbs.x, bottomRightAbs.y - topLeftAbs.y)
        return RectangleAbsolute(topLeftAbs.x, topLeftAbs.y, sizeAbs.x, sizeAbs.y)
    }
}