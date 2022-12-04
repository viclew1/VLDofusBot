package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

object MetamobTradeUIUtil {

    val playerTradeMonsters = mutableStateOf<List<MetamobMonster>>(emptyList())
    val otherGuyTradeMonsters = mutableStateOf<List<MetamobMonster>>(emptyList())
    val tradeOpened = mutableStateOf(false)

}