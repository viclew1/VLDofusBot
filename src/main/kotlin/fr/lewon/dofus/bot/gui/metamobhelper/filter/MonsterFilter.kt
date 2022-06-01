package fr.lewon.dofus.bot.gui.metamobhelper.filter

import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonster
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class MonsterFilter(val parameter: DofusBotParameter, val isMonsterValidFun: (String, MetamobMonster) -> Boolean)