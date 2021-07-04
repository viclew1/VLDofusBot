package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import kotlin.math.roundToInt

object ConverterUtil {

    private const val PRECISION = 10000.0f

    /**
     * Converts a point from simple coordinates to relative coordinates.
     * @param point - Point in simple coordinates.
     * @return Point in relative coordinates.
     */
    fun toPointRelative(point: PointAbsolute): PointRelative {
        val x: Float = (point.x - GameInfo.bounds.x).toFloat() / GameInfo.bounds.width.toFloat()
        val y: Float = (point.y - GameInfo.bounds.y).toFloat() / GameInfo.bounds.height.toFloat()
        return PointRelative((x * PRECISION) / PRECISION, (y * PRECISION) / PRECISION)
    }

    /**
     * Converts a point from relative coordinates to simple coordinates.
     * @param point - Point in relative coordinates.
     * @return Point in simple coordinates.
     */
    fun toPointAbsolute(point: PointRelative): PointAbsolute {
        val x: Float = point.x * GameInfo.bounds.width + GameInfo.bounds.x
        val y: Float = point.y * GameInfo.bounds.height + GameInfo.bounds.y
        return PointAbsolute(x.roundToInt(), y.roundToInt())
    }

}