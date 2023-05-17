package fr.lewon.dofus.bot.gui.main.status

data class StatusBarUIState(
    val oldMessages: List<String> = emptyList(),
    val currentStatus: String = ""
)