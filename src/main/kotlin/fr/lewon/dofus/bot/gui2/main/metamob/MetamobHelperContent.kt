package fr.lewon.dofus.bot.gui2.main.metamob

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.main.metamob.filter.MetamobFiltersContent
import fr.lewon.dofus.bot.gui2.main.metamob.monsters.MetamobMonstersContent
import fr.lewon.dofus.bot.gui2.main.metamob.stats.MetamobStatsContent

@Composable
fun MetamobHelperContent() {
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(300.dp)) {
            Box(Modifier.fillMaxHeight().weight(1f)) {
                MetamobFiltersContent()
            }
            Box(Modifier.height(200.dp)) {
                MetamobStatsContent()
            }
        }
        Box(Modifier.fillMaxSize()) {
            MetamobMonstersContent()
        }
    }
}