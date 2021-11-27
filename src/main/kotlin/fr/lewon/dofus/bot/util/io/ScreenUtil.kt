package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color
import java.awt.image.BufferedImage


object ScreenUtil {

    fun getPixelColor(point: PointAbsolute, fullScreenshot: BufferedImage): Color {
        return Color(fullScreenshot.getRGB(point.x, point.y))
    }

    fun getPixelColor(
        gameInfo: GameInfo,
        point: PointRelative,
        fullScreenshot: BufferedImage = JNAUtil.takeCapture(gameInfo)
    ): Color {
        return getPixelColor(ConverterUtil.toPointAbsolute(gameInfo, point), fullScreenshot)
    }

    fun isBetween(gameInfo: GameInfo, point: PointRelative, min: Color, max: Color): Boolean {
        return isBetween(getPixelColor(gameInfo, point), min, max)
    }

    fun isBetween(color: Color, min: Color, max: Color): Boolean {
        return color.red in min.red..max.red &&
                color.green in min.green..max.green &&
                color.blue in min.blue..max.blue
    }

    fun colorCount(
        gameInfo: GameInfo,
        bounds: RectangleRelative,
        min: Color,
        max: Color,
        fullScreenshot: BufferedImage = JNAUtil.takeCapture(gameInfo)
    ): Int {
        return colorCount(gameInfo, ConverterUtil.toRectangleAbsolute(gameInfo, bounds), min, max, fullScreenshot)
    }

    fun colorCount(
        gameInfo: GameInfo,
        bounds: RectangleAbsolute,
        min: Color,
        max: Color,
        fullScreenshot: BufferedImage = JNAUtil.takeCapture(gameInfo)
    ): Int {
        return colorCount(gameInfo, bounds, fullScreenshot) { isBetween(it, min, max) }
    }

    private fun colorCount(
        gameInfo: GameInfo,
        bounds: RectangleAbsolute,
        fullScreenshot: BufferedImage = JNAUtil.takeCapture(gameInfo),
        acceptColorCondition: (Color) -> Boolean
    ): Int {
        var cpt = 0
        val minX = 0
        val minY = 0
        val maxX = fullScreenshot.width
        val maxY = fullScreenshot.height
        for (x in bounds.x until bounds.x + bounds.width) {
            for (y in bounds.y until bounds.y + bounds.height) {
                if (x in minX until maxX && y in minY until maxY) {
                    val color = getPixelColor(PointAbsolute(x, y), fullScreenshot)
                    if (acceptColorCondition(color)) {
                        cpt++
                    }
                }
            }
        }
        return cpt
    }
}