package fr.lewon.dofus.bot.gui2.main.scripts.status

data class StatusBarUIState(
    val oldMessages: List<String> = emptyList(),
    val currentStatus: String = ""
)