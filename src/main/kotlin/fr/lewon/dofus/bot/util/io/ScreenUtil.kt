package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage


object ScreenUtil {

    /**
     * Returns the color of the pixel at a given location.
     * @param point - Location of the pixel on the screen in simple coordinates.
     * @return Color of the pixel.
     */
    fun getPixelColor(point: PointAbsolute): Color {
        return Robot().getPixelColor(point.x, point.y)
    }

    /**
     * Returns the color of the pixel at a given location.
     * @param point - Location of the pixel on the screen in simple coordinates.
     * @return Color of the pixel.
     */
    fun getPixelColor(point: PointAbsolute, fullScreenshot: BufferedImage): Color {
        return Color(fullScreenshot.getRGB(point.x, point.y))
    }

    /**
     * Returns the color of the pixel at a given location.
     * @param point - Location of the pixel on the screen in draughtboard coordinates.
     * @return Color of the pixel.
     */
    fun getPixelColor(point: PointRelative, fullScreenshot: BufferedImage = takeFullScreenshot()): Color {
        return getPixelColor(ConverterUtil.toPointAbsolute(point), fullScreenshot)
    }

    /**
     * Performs a screenshot of the whole screen
     * @return Screenshot as BufferedImage
     */
    fun takeFullScreenshot(): BufferedImage {
        var screenRect = Rectangle(0, 0, 0, 0)
        for (gd in GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices) {
            screenRect = screenRect.union(gd.defaultConfiguration.bounds)
        }
        return Robot().createScreenCapture(screenRect)
    }

    /**
     * Checks whether a color is in a given interval.
     * @param point - Location of the pixel on the screen in simple coordinates.
     * @param min - Minimal color.
     * @param max - Maximal color.
     * @return `true` if the color is in the interval, `false` otherwise.
     */
    fun isBetween(point: PointAbsolute, min: Color, max: Color): Boolean {
        return isBetween(getPixelColor(point), min, max)
    }

    /**
     * Checks whether a color is in a given interval.
     * @param point - Location of the pixel on the screen in simple coordinates.
     * @param min - Minimal color.
     * @param max - Maximal color.
     * @return `true` if the color is in the interval, `false` otherwise.
     */
    fun isBetween(point: PointRelative, min: Color, max: Color): Boolean {
        return isBetween(getPixelColor(point), min, max)
    }

    /**
     * Checks whether a color is in a given interval.
     * @param color - Color of the pixel to check.
     * @param min - Minimal color.
     * @param max - Maximal color.
     * @return `true` if the color is in the interval, `false` otherwise.
     */
    fun isBetween(color: Color, min: Color, max: Color): Boolean {
        return color.red in min.red..max.red &&
            color.green in min.green..max.green &&
            color.blue in min.blue..max.blue
    }

    /**
     * Waits for a pixel to be in a given interval of color.
     * @param point - Location of the pixel in simple coordinates.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @param timeOut - Time in ms before timeout.
     * @return New color of the pixel, `null` if timeout.
     */
    fun waitForColor(
        point: PointAbsolute, min: Color, max: Color,
        timeOut: Int = ConfigManager.config.globalTimeout * 1000
    ): Boolean {
        return WaitUtil.waitUntil({ isBetween(getPixelColor(point), min, max) }, timeOut)
    }

    /**
     * Waits for a pixel to be in a given interval of color.
     * @param point - Location of the pixel in relative coordinates.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @param timeOut - Time in ms before timeout.
     * @return New color of the pixel, `null` if timeout.
     */
    fun waitForColor(
        point: PointRelative, min: Color, max: Color,
        timeOut: Int = ConfigManager.config.globalTimeout * 1000
    ): Boolean {
        return waitForColor(ConverterUtil.toPointAbsolute(point), min, max, timeOut)
    }

    /**
     * Counts the amount of pixels corresponding to any of the given colors in the given rectangle
     * @param bounds - Rectangle in which colors will be searched.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @return Amount of pixels with given color
     */
    fun colorCount(
        bounds: RectangleRelative,
        min: Color,
        max: Color,
        fullScreenshot: BufferedImage = takeFullScreenshot()
    ): Int {
        return colorCount(ConverterUtil.toRectangleAbsolute(bounds), min, max, fullScreenshot)
    }

    /**
     * Counts the amount of pixels corresponding to any of the given colors in the given rectangle
     * @param bounds - Rectangle in which colors will be searched.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @return Amount of pixels with given color
     */
    fun colorCount(
        bounds: RectangleAbsolute,
        min: Color,
        max: Color,
        fullScreenshot: BufferedImage = takeFullScreenshot()
    ): Int {
        return colorCount(bounds, fullScreenshot) { isBetween(it, min, max) }
    }

    private fun colorCount(
        bounds: RectangleAbsolute,
        fullScreenshot: BufferedImage = takeFullScreenshot(),
        acceptColorCondition: (Color) -> Boolean
    ): Int {
        var cpt = 0
        for (x in bounds.x until bounds.x + bounds.width) {
            for (y in bounds.y until bounds.y + bounds.height) {
                val color = getPixelColor(PointAbsolute(x, y), fullScreenshot)
                if (acceptColorCondition.invoke(color)) {
                    cpt++
                }
            }
        }
        return cpt
    }
}