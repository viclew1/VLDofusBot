package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import kotlin.math.roundToInt

object ConverterUtil {

    private const val PRECISION = 10000.0f

    /**
     * Converts a point from absolute coordinates to relative coordinates.
     * @param point - Point in absolute coordinates.
     * @return Point in relative coordinates.
     */
    fun toPointRelative(point: PointAbsolute): PointRelative {
        val x = (point.x - GameInfo.bounds.x).toFloat() / GameInfo.bounds.width.toFloat()
        val y = (point.y - GameInfo.bounds.y).toFloat() / GameInfo.bounds.height.toFloat()
        return PointRelative((x * PRECISION) / PRECISION, (y * PRECISION) / PRECISION)
    }

    /**
     * Converts a point from relative coordinates to absolute coordinates.
     * @param point - Point in relative coordinates.
     * @return Point in simple coordinates.
     */
    fun toPointAbsolute(point: PointRelative): PointAbsolute {
        val x = point.x * GameInfo.bounds.width + GameInfo.bounds.x
        val y = point.y * GameInfo.bounds.height + GameInfo.bounds.y
        return PointAbsolute(x.roundToInt(), y.roundToInt())
    }

    /**
     * Converts a rectangle from absolute coordinates to relative coordinates.
     * @param rectangle - Rectangle in absolute coordinates.
     * @return Rectangle in relative coordinates.
     */
    fun toRectangleRelative(rectangle: RectangleAbsolute): RectangleRelative {
        val x1 = (rectangle.x - GameInfo.bounds.x).toFloat() / GameInfo.bounds.width.toFloat()
        val y1 = (rectangle.y - GameInfo.bounds.y).toFloat() / GameInfo.bounds.height.toFloat()
        val x2 = (rectangle.x + rectangle.width - GameInfo.bounds.x).toFloat() / GameInfo.bounds.width.toFloat()
        val y2 = (rectangle.y + rectangle.height - GameInfo.bounds.y).toFloat() / GameInfo.bounds.height.toFloat()
        return RectangleRelative(
            (x1 * PRECISION) / PRECISION,
            (y1 * PRECISION) / PRECISION,
            ((x2 - x1) * PRECISION) / PRECISION,
            ((y2 - y1) * PRECISION) / PRECISION
        )
    }

    /**
     * Converts a rectangle from relative coordinates to absolute coordinates.
     * @param rectangle - Rectangle in relative coordinates.
     * @return Rectangle in absolute coordinates.
     */
    fun toRectangleAbsolute(rectangle: RectangleRelative): RectangleAbsolute {
        val x1 = rectangle.x * GameInfo.bounds.width + GameInfo.bounds.x
        val y1 = rectangle.y * GameInfo.bounds.height + GameInfo.bounds.y
        val x2 = (rectangle.x + rectangle.width) * GameInfo.bounds.width + GameInfo.bounds.x
        val y2 = (rectangle.y + rectangle.height) * GameInfo.bounds.height + GameInfo.bounds.y
        return RectangleAbsolute(x1.roundToInt(), y1.roundToInt(), (x2 - x1).roundToInt(), (y2 - y1).roundToInt())
    }

}