package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.model.characters.DofusCharacter

enum class LoggerUIType(
    val label: String,
    val canBePaused: Boolean,
    val loggerGetter: (DofusCharacter) -> VldbLogger
) {
    EXECUTION("Execution logs", false, { it.executionLogger }),
    SNIFFER("Sniffer logs", true, { it.snifferLogger })
}