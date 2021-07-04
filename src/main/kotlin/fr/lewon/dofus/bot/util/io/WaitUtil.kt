package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager

object WaitUtil {

    /**
     * Wait for a given time in ms.
     * @param time - Time to wait iin ms.
     */
    fun sleep(time: Long) {
        Thread.sleep(time)
    }

    /**
     * Wait for a given time in ms.
     * @param time - Time to wait iin ms.
     */
    fun sleep(time: Int) {
        sleep(time.toLong())
    }

    /**
     * Wait until the given condition returns true, returns true if the condition is verified before given timeOut
     */
    fun waitUntil(
        condition: () -> Boolean, timeOutMillis: Int = DTBConfigManager.config.globalTimeout * 1000
    ): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            if (condition.invoke()) {
                return true
            }
            sleep(100)
        }
        return false
    }
}