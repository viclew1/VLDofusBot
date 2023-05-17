package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.logs

enum class LoggerUIType(
    val label: String,
    val canBePaused: Boolean,
    val maxCapacity: Int,
) {
    EXECUTION("Execution logs", false, 10),
    SNIFFER("Sniffer logs", true, 100)
}