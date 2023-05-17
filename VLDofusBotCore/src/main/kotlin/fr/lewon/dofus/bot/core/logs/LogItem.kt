package fr.lewon.dofus.bot.core.logs

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicLong

class LogItem(val parent: LogItem?, val message: String, val description: String = "", subItemCapacity: Int) {

    companion object {
        private val ID_GENERATOR = AtomicLong(0)
    }

    val id = ID_GENERATOR.incrementAndGet()
    val subLogs = ArrayBlockingQueue<LogItem>(subItemCapacity)
    var closeMessage: String? = null

    override fun toString(): String {
        return displayLog(0)
    }

    private fun displayLog(depth: Int): String {
        var prefix = ""
        for (i in 0 until depth) {
            prefix += " - "
        }
        var ret = prefix + message
        for (subLog in subLogs) {
            ret += "\n" + subLog.displayLog(depth + 1)
        }
        if (closeMessage != null) {
            ret += if (subLogs.isEmpty()) {
                " $closeMessage"
            } else {
                "\n$prefix$closeMessage"
            }
        }
        return ret
    }

}