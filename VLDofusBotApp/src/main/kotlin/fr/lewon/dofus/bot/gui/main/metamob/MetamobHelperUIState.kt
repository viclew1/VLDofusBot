package fr.lewon.dofus.bot.gui.main.metamob

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

data class MetamobHelperUIState(
    val metamobMonsters: List<MetamobMonster> = emptyList(),
    val filterValues: ParameterValues = ParameterValues(),
    val priceByArchmonsterId: Map<Int, Long> = HashMap(),
    val refreshingPrices: Boolean = false,
    val lastPriceRefreshTimeMillis: Long? = null,
    val playerTradeMonsters: List<MetamobMonster> = emptyList(),
    val otherGuyTradeMonsters: List<MetamobMonster> = emptyList(),
    val errorMessage: String = "",
    val hoveredMonster: MetamobMonster? = null,
)