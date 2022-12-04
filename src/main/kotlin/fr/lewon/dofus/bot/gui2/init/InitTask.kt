package fr.lewon.dofus.bot.gui2.init

data class InitTask(
    val label: String,
    var success: Boolean = false,
    val executionFunction: () -> Unit
)