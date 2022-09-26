package fr.lewon.dofus.bot.util.network

import java.util.*

object NetworkAutoUpdater {

    private val timer = Timer()
    private var started = false

    fun start() {
        GameSnifferUtil.updateNetwork()
        if (!started) {
            timer.schedule(object : TimerTask() {
                override fun run() {
                    GameSnifferUtil.updateNetwork()
                }
            }, 5000L, 5000L)
            started = true
        }
    }

}