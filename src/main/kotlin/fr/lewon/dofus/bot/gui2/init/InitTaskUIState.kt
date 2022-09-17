package fr.lewon.dofus.bot.gui2.init

data class InitTaskUIState(
    val label: String,
    val executed: Boolean = false,
    val success: Boolean = false,
    val executing: Boolean = false,
    val function: () -> Unit
)