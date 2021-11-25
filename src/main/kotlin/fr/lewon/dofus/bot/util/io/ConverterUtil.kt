package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.core.manager.ui.UIBounds
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
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
        val x = (point.x - gameInfo.bounds.x).toFloat() / gameInfo.bounds.width.toFloat()
        val y = (point.y - gameInfo.bounds.y).toFloat() / gameInfo.bounds.height.toFloat()
        return PointRelative((x * PRECISION) / PRECISION, (y * PRECISION) / PRECISION)
    }

    fun toPointAbsolute(gameInfo: GameInfo, point: PointRelative): PointAbsolute {
        val x = point.x * gameInfo.bounds.width + gameInfo.bounds.x
        val y = point.y * gameInfo.bounds.height + gameInfo.bounds.y
        return PointAbsolute(x.roundToInt(), y.roundToInt())
    }

    fun toRectangleRelative(gameInfo: GameInfo, rectangle: RectangleAbsolute): RectangleRelative {
        val x1 = (rectangle.x - gameInfo.bounds.x).toFloat() / gameInfo.bounds.width.toFloat()
        val y1 = (rectangle.y - gameInfo.bounds.y).toFloat() / gameInfo.bounds.height.toFloat()
        val x2 = (rectangle.x + rectangle.width - gameInfo.bounds.x).toFloat() / gameInfo.bounds.width.toFloat()
        val y2 = (rectangle.y + rectangle.height - gameInfo.bounds.y).toFloat() / gameInfo.bounds.height.toFloat()
        return RectangleRelative(
            (x1 * PRECISION) / PRECISION,
            (y1 * PRECISION) / PRECISION,
            ((x2 - x1) * PRECISION) / PRECISION,
            ((y2 - y1) * PRECISION) / PRECISION
        )
    }

    fun toRectangleAbsolute(gameInfo: GameInfo, rectangle: RectangleRelative): RectangleAbsolute {
        val x1 = rectangle.x * gameInfo.bounds.width + gameInfo.bounds.x
        val y1 = rectangle.y * gameInfo.bounds.height + gameInfo.bounds.y
        val x2 = (rectangle.x + rectangle.width) * gameInfo.bounds.width + gameInfo.bounds.x
        val y2 = (rectangle.y + rectangle.height) * gameInfo.bounds.height + gameInfo.bounds.y
        return RectangleAbsolute(x1.roundToInt(), y1.roundToInt(), (x2 - x1).roundToInt(), (y2 - y1).roundToInt())
    }

}