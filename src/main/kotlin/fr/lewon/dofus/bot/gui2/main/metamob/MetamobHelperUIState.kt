package fr.lewon.dofus.bot.gui2.main.metamob

import fr.lewon.dofus.bot.gui2.main.metamob.filter.MonsterFilter
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

data class MetamobHelperUIState(
    val metamobMonsters: List<MetamobMonster> = emptyList(),
    val valueByFilter: Map<MonsterFilter, String> = MonsterFilter.values().associateWith { it.parameter.defaultValue },
    val filteredMonsters: List<MetamobMonster> = emptyList(),
    val errorMessage: String = ""
)