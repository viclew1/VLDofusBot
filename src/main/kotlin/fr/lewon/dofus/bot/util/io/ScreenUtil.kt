package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import net.sourceforge.tess4j.Tesseract
import java.awt.*
import java.awt.image.BufferedImage
import java.util.*

object ScreenUtil {

    /**
     * Returns the color of the pixel at a given location.
     * @param point - Location of the pixel on the screen in simple coordinates.
     * @return Color of the pixel.
     */
    fun getPixelColor(point: PointAbsolute): Color {
        val robot = Robot()
        return robot.getPixelColor(point.x, point.y)
    }

    /**
     * Returns the color of the pixel at a given location.
     * @param point - Location of the pixel on the screen in draughtboard coordinates.
     * @return Color of the pixel.
     */
    fun getPixelColor(point: PointRelative): Color {
        return getPixelColor(ConverterUtil.toPointAbsolute(point))
    }

    /**
     * Performs a pixel research for a given area.
     * @param topLeftHandCorner - Top left hand corner of the research area in relative coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the research area in relative coordinates.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @return Location of the first pixel matching the color criteria in relative coordinates. `null` if no pixel match the research criteria.
     */
    fun searchPixel(
        topLeftHandCorner: PointRelative,
        bottomRightHandCorner: PointRelative,
        min: Color,
        max: Color
    ): PointRelative? {
        val point1 = ConverterUtil.toPointAbsolute(topLeftHandCorner)
        val point2 = ConverterUtil.toPointAbsolute(bottomRightHandCorner)
        var color: Color
        var point: PointAbsolute
        for (y in point1.y..point2.y) {
            for (x in point1.x..point2.x) {
                point = PointAbsolute(x, y)
                color = getPixelColor(point)
                if (isBetween(color, min, max)) {
                    return ConverterUtil.toPointRelative(point)
                }
            }
        }
        return null
    }

    /**
     * Performs a pixel research for a given area.
     * @param topLeftHandCorner - Top left hand corner of the research area in relative coordinates.
     * @param bottomRightHandCorner - Bottom left hand corner of the research area in relative coordinates.
     * @param min - Minimum color.
     * @param max - Maximum color.
     * @return List of pixels matching the color criteria in relative coordinates. `null` if no pixel match the research criteria.
     */
    fun searchPixels(
        topLeftHandCorner: PointRelative,
        bottomRightHandCorner: PointRelative,
        min: Color,
        max: Color
    ): ArrayList<PointRelative> {
        val point1 = ConverterUtil.toPointAbsolute(topLeftHandCorner)
        val point2 = ConverterUtil.toPointAbsolute(bottomRightHandCorner)
        val points = ArrayList<PointRelative>()
        var color: Color
        var point: PointAbsolute
        for (y in point1.y..point2.y) {
            for (x in point1.x..point2.x) {
                point = PointAbsolute(x, y)
                color = getPixelColor(point)
                if (isBetween(color, min, max)) {
                    points.add(ConverterUtil.toPointRelative(point))
                }
            }
        }
        return points
    }

    /**
     * Performs a screenshot for a given area.
     * @param rectangle - Screenshot area in simple coordinates.
     * @return Screenshot of the rectangle area.
     */
    fun takeScreenshot(rectangle: Rectangle): BufferedImage {
        val robot = Robot()
        return robot.createScreenCapture(rectangle)
    }

    /**
     * Performs a screenshot for the game frame area.
     * @param rectangle - Screenshot area in simple coordinates.
     * @return Screenshot of the game frame area.
     */
    fun takeScreenshot(): BufferedImage {
        return takeScreenshot(GameInfo.completeBounds)
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
     * Waits for a pixel to change color.
     * @param point - Location of the pixel in simple coordinates.
     * @param timeOut - Time in ms before timeout.
     * @return New color of the pixel, `null` if timeout.
     */
    fun waitForChangingPixel(point: PointAbsolute, timeOut: Int): Color {
        var newColor: Color
        val color: Color = getPixelColor(point)
        do {
            newColor = getPixelColor(point)
            WaitUtil.sleep(100)
        } while (color == newColor)
        return newColor
    }

    /**
     * Waits for a pixel to change color.
     * @param point - Location of the pixel in relative coordinates.
     * @param timeOut - Time in ms before timeout.
     * @return New color of the pixel, `null` if timeout.
     */
    fun waitForChangingPixel(point: PointRelative, timeOut: Int): Color {
        return waitForChangingPixel(ConverterUtil.toPointAbsolute(point), timeOut)
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

}