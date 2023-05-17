package fr.lewon.dofus.bot.gui.main.metamob.stats

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.HorizontalSeparator
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType

@Composable
fun MetamobStatsContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        CommonText("Stats", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
        HorizontalSeparator()
        Row(Modifier.fillMaxSize()) {
            val displayedMonsters = MetamobHelperUIUtil.getFilteredMonsters()
            val archmonsters = displayedMonsters.filter { it.type == MetamobMonsterType.ARCHMONSTER }
            val bosses = displayedMonsters.filter { it.type == MetamobMonsterType.BOSS }
            val monsters = displayedMonsters.filter { it.type == MetamobMonsterType.MONSTER }
            val totalCount = displayedMonsters.size
            val totalMissing = displayedMonsters.count { it.amount <= 0 }
            val archmonstersCount = archmonsters.size
            val archmonstersMissing = archmonsters.count { it.amount <= 0 }
            val bossesCount = bosses.size
            val bossesMissing = bosses.count { it.amount <= 0 }
            val monstersCount = monsters.size
            val monstersMissing = monsters.count { it.amount <= 0 }
            Column(Modifier.align(Alignment.CenterVertically)) {
                StatRow("Total displayed : ", "$totalCount ($totalMissing missing)")
                StatRow("Archmonsters : ", "$archmonstersCount ($archmonstersMissing missing)")
                StatRow("Bosses : ", "$bossesCount ($bossesMissing missing)")
                StatRow("Monsters : ", "$monstersCount ($monstersMissing missing)")
            }
        }
    }
}

@Composable
private fun StatRow(title: String, value: String) {
    Row(Modifier.padding(5.dp)) {
        CommonText(title, Modifier.fillMaxWidth().weight(1f))
        CommonText(value)
    }
}
