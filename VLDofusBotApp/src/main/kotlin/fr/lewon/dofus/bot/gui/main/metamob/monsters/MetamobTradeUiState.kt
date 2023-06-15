package fr.lewon.dofus.bot.gui.main.metamob.monsters

import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

data class MetamobTradeUiState(
    val playerTradeMonsters: List<MetamobMonster> = emptyList(),
    val otherGuyTradeMonsters: List<MetamobMonster> = emptyList(),
    val tradeOpened: Boolean = false
)