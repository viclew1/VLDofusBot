package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import fr.lewon.dofus.bot.model.characters.DofusCharacter

data class CharacterLogsUIState(
    val character: DofusCharacter,
    val loggerType: LoggerUIType,
)