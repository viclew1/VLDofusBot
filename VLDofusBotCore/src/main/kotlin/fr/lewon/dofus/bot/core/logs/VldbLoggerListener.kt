package fr.lewon.dofus.bot.core.logs

interface VldbLoggerListener {

    fun onLogUpdated(logger: VldbLogger, logItem: LogItem)

}