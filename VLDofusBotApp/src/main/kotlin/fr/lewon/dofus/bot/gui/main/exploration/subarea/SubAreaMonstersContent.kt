package fr.lewon.dofus.bot.gui.main.exploration.subarea

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.HorizontalSeparator
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager

@Composable
fun SubAreaMonstersContent(subArea: DofusSubArea) {
    Box(Modifier.fillMaxSize()) {
        val state = rememberScrollState()
        Column(Modifier.fillMaxSize().padding(5.dp).padding(end = 8.dp).verticalScroll(state)) {
            if (subArea.monsters.isEmpty()) {
                CommonText(
                    "No monster in this sub area",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
                )
            } else {
                ResourcesListContent("Monsters", subArea.monsters.filter { !it.isMiniBoss && !it.isQuestMonster })
                ResourcesListContent("Archmonsters", subArea.monsters.filter { it.isMiniBoss })
                ResourcesListContent("Quest monsters", subArea.monsters.filter { it.isQuestMonster })
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                .background(AppColors.backgroundColor),
            adapter = rememberScrollbarAdapter(state),
        )
    }
}

@Composable
private fun ResourcesListContent(title: String, monsters: List<DofusMonster>) {
    if (monsters.isNotEmpty()) {
        HorizontalSeparator(title, modifier = Modifier.padding(vertical = 10.dp))
        for (monster in monsters) {
            SelectionContainer {
                Row {
                    OwnedIndicatorContent(monster)
                    Spacer(Modifier.width(5.dp))
                    CommonText(monster.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun RowScope.OwnedIndicatorContent(monster: DofusMonster) {
    if (MetamobHelperUIUtil.refreshingMonsters.value) {
        Box(Modifier.requiredSize(8.dp).align(Alignment.CenterVertically)) {
            CircularProgressIndicator(Modifier.fillMaxSize(), color = AppColors.primaryColor)
        }
    } else {
        val simultaneousOchers = MetamobConfigManager.readConfig().getSafeSimultaneousOchers()
        val allMetamobMonsters = MetamobHelperUIUtil.getUiStateValue().metamobMonsters
        val metamobMonster = MetamobMonstersHelper.getMetamobMonster(monster, allMetamobMonsters)
        val color = when {
            metamobMonster == null -> Color.White
            metamobMonster.amount >= simultaneousOchers -> AppColors.GREEN
            metamobMonster.amount > 0 -> AppColors.ORANGE
            else -> AppColors.RED
        }
        CommonText(metamobMonster?.amount?.toString() ?: "/", enabledColor = color)
    }
}