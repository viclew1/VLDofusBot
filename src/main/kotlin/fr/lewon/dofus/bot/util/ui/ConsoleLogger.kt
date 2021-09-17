package fr.lewon.dofus.bot.util.ui

object ConsoleLogger {

    var minLogLevel: LogLevel = LogLevel.INFO

    fun trace(str: String) {
        log(str, LogLevel.TRACE)
    }

    fun debug(str: String) {
        log(str, LogLevel.DEBUG)
    }

    fun info(str: String) {
        log(str, LogLevel.INFO)
    }

    fun warn(str: String) {
        log(str, LogLevel.WARN)
    }

    fun error(str: String) {
        log(str, LogLevel.ERROR)
    }

    private fun log(str: String, logLevel: LogLevel) {
        if (logLevel.level >= minLogLevel.level) {
            println("[${logLevel.name}] - $str")
        }
    }

}