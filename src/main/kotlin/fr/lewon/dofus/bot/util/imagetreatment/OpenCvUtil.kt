package fr.lewon.dofus.bot.util.imagetreatment

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
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
    fun getPatternBounds(captureBounds: Rectangle, pattern: Mat, minMatchValue: Double = 0.4): Rectangle? {
        clearMats()
        val ratio = GameInfo.sizeRatio
        val gameImage = bufferedImageToMat(getScaledGameImage(ratio, captureBounds))
        val matchResult = this.getMatchResult(gameImage, pattern)
        if (matchResult.maxVal < minMatchValue) return null
        return Rectangle(
            GameInfo.completeBounds.x + (matchResult.maxLoc.x * ratio).toInt(),
            GameInfo.completeBounds.y + (matchResult.maxLoc.y * ratio).toInt(),
            (pattern.cols().toFloat() * ratio).toInt(),
            (pattern.rows().toFloat() * ratio).toInt()
        )
    }

    @Synchronized
    fun getPatternBounds(pattern: Mat, minMatchValue: Double = 0.4): Rectangle? {
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
        val gameImage = ScreenUtil.takeScreenshot()
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