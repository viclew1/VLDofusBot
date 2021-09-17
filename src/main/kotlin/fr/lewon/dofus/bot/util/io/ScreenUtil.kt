package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import net.sourceforge.tess4j.Tesseract
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
     * Performs a screenshot for a given area.
     * @param rectangle - Screenshot area in simple coordinates.
     * @return Screenshot of the rectangle area.
     */
    fun takeScreenshot(rectangle: Rectangle = GameInfo.completeBounds): BufferedImage {
        val robot = Robot()
        return robot.createScreenCapture(rectangle)
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
     * Performs an Optical Character Recognition on a given area.
     * @param rectangle - OCR area in simple coordinates.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun doOCR(rectangle: Rectangle): List<String> {
        val image = takeScreenshot(rectangle)
        val tess = Tesseract()
        tess.setDatapath("tessdata")
        return tess.doOCR(image)
            .split("\n")
            .filter { it.isNotBlank() }
    }

    /**
     * Performs an Optical Character Recognition on a given area.
     * @param topLeftHandCorner - Top left hand corner of the OCR area in simple coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the OCR area in simple coordinates.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun doOCR(topLeftHandCorner: PointAbsolute, bottomRightHandCorner: PointAbsolute): List<String> {
        return doOCR(
            Rectangle(
                topLeftHandCorner.x,
                topLeftHandCorner.y,
                bottomRightHandCorner.x - topLeftHandCorner.x,
                bottomRightHandCorner.y - topLeftHandCorner.y
            )
        )
    }

    /**
     * Performs an Optical Character Recognition on a given area.
     * @param topLeftHandCorner - Top left hand corner of the OCR area in relative coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the OCR area in relative coordinates.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun doOCR(topLeftHandCorner: PointRelative, bottomRightHandCorner: PointRelative): List<String> {
        return doOCR(
            ConverterUtil.toPointAbsolute(topLeftHandCorner),
            ConverterUtil.toPointAbsolute(bottomRightHandCorner)
        )
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
     * Wait for a string to be displayed on screen.
     * @param rectangle - OCR area in simple coordinates.
     * @param regex - Regular expression that the string must contain.
     * @param timeOut - Time in ms before timeout.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun waitForOCR(rectangle: Rectangle, regex: String, timeOut: Int): List<String> {
        var text: List<String>
        do {
            text = doOCR(rectangle)
            WaitUtil.sleep(100)
        } while (!text.contains(regex))
        return text
    }

    /**
     * Wait for a string to be displayed on screen.
     * @param topLeftHandCorner - Top left hand corner of the OCR area in simple coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the OCR area in simple coordinates.
     * @param regex - Regular expression that the string must contain.
     * @param timeOut - Time in ms before timeout.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun waitForOCR(
        topLeftHandCorner: PointAbsolute,
        bottomRightHandCorner: PointAbsolute,
        regex: String,
        timeOut: Int
    ): List<String> {
        return waitForOCR(
            Rectangle(
                topLeftHandCorner.x,
                topLeftHandCorner.y,
                bottomRightHandCorner.x - topLeftHandCorner.x,
                bottomRightHandCorner.y - topLeftHandCorner.y
            ), regex, timeOut
        )
    }

    /**
     * Wait for a string to be displayed on screen.
     * @param topLeftHandCorner - Top left hand corner of the OCR area in relative coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the OCR area in relative coordinates.
     * @param regex - Regular expression that the string must contain.
     * @param timeOut - Time in ms before timeout.
     * @return String recognized on screen, `null` if nothing have been found.
     */
    fun waitForOCR(
        topLeftHandCorner: PointRelative,
        bottomRightHandCorner: PointRelative,
        regex: String,
        timeOut: Int
    ): List<String> {
        return waitForOCR(
            ConverterUtil.toPointAbsolute(topLeftHandCorner),
            ConverterUtil.toPointAbsolute(bottomRightHandCorner),
            regex,
            timeOut
        )
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
        timeOut: Int = DTBConfigManager.config.globalTimeout * 1000
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
        timeOut: Int = DTBConfigManager.config.globalTimeout * 1000
    ): Boolean {
        return waitForColor(ConverterUtil.toPointAbsolute(point), min, max, timeOut)
    }

    /**
     * Counts the amount of pixels corresponding to any of the given colors in the given rectangle
     * @param bounds - Rectangle in which colors will be searched.
     * @param colors - List of colors RGB
     * @return Amount of pixels with given color
     */
    fun colorCount(
        bounds: RectangleRelative,
        colors: List<Int>,
        fullScreenshot: BufferedImage = takeFullScreenshot()
    ): Int {
        return colorCount(ConverterUtil.toRectangleAbsolute(bounds), colors, fullScreenshot)
    }

    /**
     * Counts the amount of pixels corresponding to any of the given colors in the given rectangle
     * @param bounds - Rectangle in which colors will be searched.
     * @param colors - List of colors RGB
     * @return Amount of pixels with given color
     */
    fun colorCount(
        bounds: RectangleAbsolute,
        colors: List<Int>,
        fullScreenshot: BufferedImage = takeFullScreenshot()
    ): Int {
        return colorCount(bounds, fullScreenshot) { colors.contains(it.rgb) }
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