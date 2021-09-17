package fr.lewon.dofus.bot.util.imagetreatment

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.io.ScreenUtil
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


object OpenCvUtil {

    @Synchronized
    fun getPatternBounds(captureBounds: Rectangle, pattern: Mat, minMatchValue: Double = 0.4): RectangleAbsolute? {
        clearMats()
        val ratio = GameInfo.sizeRatio
        val gameImage = bufferedImageToMat(getScaledGameImage(ratio, captureBounds))
        val matchResult = this.getMatchResult(gameImage, pattern)
        if (matchResult.maxVal < minMatchValue) return null
        return RectangleAbsolute(
            captureBounds.x + (matchResult.maxLoc.x * ratio).toInt(),
            captureBounds.y + (matchResult.maxLoc.y * ratio).toInt(),
            (pattern.cols().toFloat() * ratio).toInt(),
            (pattern.rows().toFloat() * ratio).toInt()
        )
    }

    fun getFrameBounds(
        topFrameMat: Mat,
        botFrameMat: Mat,
        leftFrameMat: Mat,
        rightFrameMat: Mat,
        minMatchValue: Double = 0.25
    ): RectangleAbsolute? {
        clearMats()
        val ratio = GameInfo.sizeRatio
        val gameImage = getScaledGameImage(ratio, GameInfo.completeBounds)
        val gameMat = bufferedImageToMat(gameImage)
        val topMatchResult = this.getMatchResult(gameMat, topFrameMat)
        if (topMatchResult.maxVal < minMatchValue) return null
        val botMatchResult = this.getMatchResult(gameMat, botFrameMat)
        if (botMatchResult.maxVal < minMatchValue) return null
        val leftMatchResult = this.getMatchResult(gameMat, leftFrameMat)
        if (leftMatchResult.maxVal < minMatchValue) return null
        val rightMatchResult = this.getMatchResult(gameMat, rightFrameMat)
        if (rightMatchResult.maxVal < minMatchValue) return null

        val x = leftMatchResult.maxLoc.x.toInt()
        val y = topMatchResult.maxLoc.y.toInt()
        val w = rightMatchResult.maxLoc.x.toInt() + rightFrameMat.cols() - x
        val h = botMatchResult.maxLoc.y.toInt() + botFrameMat.rows() - y

        if (w <= 0 || h <= 0) {
            return null
        }
        return RectangleAbsolute(
            GameInfo.completeBounds.x + (x.toFloat() * ratio).toInt(),
            GameInfo.completeBounds.y + (y.toFloat() * ratio).toInt(),
            (w.toFloat() * ratio).toInt(),
            (h.toFloat() * ratio).toInt()
        )
    }

    @Synchronized
    fun getPatternBounds(pattern: Mat, minMatchValue: Double = 0.4): RectangleAbsolute? {
        return getPatternBounds(GameInfo.completeBounds, pattern, minMatchValue)
    }

    @Synchronized
    fun getPatternCenter(pattern: Mat, minMatchValue: Double = 0.4): PointAbsolute? {
        return getPatternBounds(pattern, minMatchValue)?.let {
            PointAbsolute(it.x + it.width / 2, it.y + it.height / 2)
        }
    }

    private fun bufferedImageToMat(image: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", byteArrayOutputStream)
        byteArrayOutputStream.flush()
        return Imgcodecs.imdecode(
            MatOfByte(*byteArrayOutputStream.toByteArray()),
            Imgcodecs.CV_LOAD_IMAGE_UNCHANGED
        )
    }

    private fun getMatchResult(pattern: Mat, templateMat: Mat): Core.MinMaxLocResult {
        val outputImageHunt = Mat()
        Imgproc.matchTemplate(
            pattern,
            templateMat,
            outputImageHunt,
            Imgproc.TM_CCOEFF_NORMED
        )
        return Core.minMaxLoc(outputImageHunt)
    }

    private fun getScaledGameImage(sizeRatio: Float, captureBounds: Rectangle): BufferedImage {
        val gameImage = ScreenUtil.takeScreenshot(captureBounds)
        val scale = 1f / sizeRatio
        val scaledImage = BufferedImage(
            (scale * gameImage.width.toFloat()).toInt(),
            (scale * gameImage.height.toFloat()).toInt(),
            gameImage.type
        )

        val g = scaledImage.graphics as Graphics2D
        g.scale(scale.toDouble(), scale.toDouble())
        g.drawImage(gameImage, 0, 0, null)
        g.dispose()
        return scaledImage
    }

    private fun clearMats() {
        System.gc()
    }

}