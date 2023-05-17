package fr.lewon.dofus.bot.gui.init

data class InitUIState(
    val currentInitTask: InitTask,
    val executing: Boolean = false,
    val error: String = "",
)