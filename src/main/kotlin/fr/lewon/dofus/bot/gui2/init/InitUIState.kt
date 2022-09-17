package fr.lewon.dofus.bot.gui2.init

data class InitUIState(
    val initSuccess: Boolean = false,
    val errorsOnInit: Boolean = false,
    val errors: List<String> = emptyList(),
)