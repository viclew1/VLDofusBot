package fr.lewon.dofus.bot.util

object MatFlusher {

    @Synchronized
    fun releaseAll() {
        System.gc()
    }

}