package fr.lewon.dofus.bot.util

import org.imgscalr.Scalr
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


object ImageUtil {

    @Synchronized
    fun bufferedImageToMat(image: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", byteArrayOutputStream)
        byteArrayOutputStream.flush()
        return Imgcodecs.imdecode(
            MatOfByte(*byteArrayOutputStream.toByteArray()),
            Imgcodecs.CV_LOAD_IMAGE_UNCHANGED
        ).also { MatFlusher.registerMat(it) }
    }

    @Synchronized
    fun matToBufferedImage(matrix: Mat): BufferedImage {
        val mob = MatOfByte().also { MatFlusher.registerMat(it) }
        Imgcodecs.imencode(".png", matrix, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }

    @Synchronized
    fun resizeImage(img: BufferedImage, ratio: Int): BufferedImage {
        val newMaxSize = Dimension(img.width * ratio, img.height * ratio)
        return Scalr.resize(
            img, Scalr.Method.QUALITY,
            newMaxSize.width, newMaxSize.height
        )
    }

    @Synchronized
    fun resizeImage(img: BufferedImage, width: Int, height: Int): BufferedImage {
        val newImg = BufferedImage(width, height, img.type)
        newImg.graphics.drawImage(img, 0, 0, width, height, null)
        return newImg
    }

    @Synchronized
    fun resizeImageKeepRatio(img: BufferedImage, width: Int, height: Int): BufferedImage {
        val newMaxSize = Dimension(width, height)
        return Scalr.resize(
            img, Scalr.Method.QUALITY,
            newMaxSize.width, newMaxSize.height
        )
    }

}