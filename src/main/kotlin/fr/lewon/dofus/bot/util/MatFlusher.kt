package fr.lewon.dofus.bot.util

import org.opencv.core.Mat

object MatFlusher {

    private val mats = ArrayList<Mat>()

    fun registerMat(mat: Mat) {
        mats.add(mat)
    }

    fun releaseAll() {
        mats.forEach { it.release() }
        mats.clear()
    }

}