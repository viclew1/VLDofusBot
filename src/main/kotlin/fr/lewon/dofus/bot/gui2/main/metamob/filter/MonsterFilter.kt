package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class MonsterFilter(val parameter: DofusBotParameter, val isMonsterValidFun: (String, MetamobMonster) -> Boolean)