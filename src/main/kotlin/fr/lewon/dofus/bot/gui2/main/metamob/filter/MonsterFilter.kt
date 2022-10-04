package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

class MonsterFilter(val parameter: DofusBotParameter, val isMonsterValidFun: (String, MetamobMonster) -> Boolean)