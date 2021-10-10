package fr.lewon.dofus.bot.util.imagetreatment

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.File

enum class MatManager(private val imgPath: String) {

    TOP_HUNT_MAT("/pattern/hunt_frame_top.png"),
    LEFT_HUNT_MAT("/pattern/hunt_frame_left.png"),
    RIGHT_HUNT_MAT("/pattern/hunt_frame_right.png"),
    BOT_HUNT_MAT("/pattern/hunt_frame_bot.png"),
    BOT_FIGHT_HUNT_MAT("/pattern/hunt_frame_fight_bot.png"),
    TOP_RUNEFORGE_MAT("/pattern/runeforge_frame_top.png"),
    TOP_ZAAP_MAT("/pattern/zaap_frame_top.png"),
    AP_MAT("/pattern/AP.png"),
    ORDER_ASC_MAT("/pattern/order_asc.png"),
    CLOSE_BATTLE_RESULT_MAT("/pattern/close_battle_result.png")
    ;

    fun buildMat(): Mat {
        return Imgcodecs.imread(File(javaClass.getResource(imgPath).toURI()).absolutePath)
    }
}