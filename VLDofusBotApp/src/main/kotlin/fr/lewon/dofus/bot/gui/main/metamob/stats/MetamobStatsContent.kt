package fr.lewon.dofus.bot.gui.main.metamob.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType

@Composable
fun MetamobStatsContent(ocherIndex: Int) {
    Column {
        Row(Modifier.fillMaxSize()) {
            val metamobMonsters = MetamobHelperUIUtil.uiState.value.metamobMonsters
            val archmonsters = metamobMonsters.filter { it.type == MetamobMonsterType.ARCHMONSTER }
            val bosses = metamobMonsters.filter { it.type == MetamobMonsterType.BOSS }
            val monsters = metamobMonsters.filter { it.type == MetamobMonsterType.MONSTER }
            Column(Modifier.align(Alignment.CenterVertically)) {
                StatRow("Total : ", metamobMonsters, ocherIndex)
                StatRow("Archmonsters : ", archmonsters, ocherIndex)
                StatRow("Bosses : ", bosses, ocherIndex)
                StatRow("Monsters : ", monsters, ocherIndex)
            }
        }
    }
}

@Composable
private fun StatRow(title: String, monsters: List<MetamobMonster>, ocherIndex: Int) {
    val count = monsters.size
    val missing = monsters.count { it.amount < ocherIndex }
    val owned = count - missing
    Row(Modifier.padding(vertical = 5.dp)) {
        Row(Modifier.width(20.dp).height(15.dp).padding(end = 5.dp)) {
            if (MetamobHelperUIUtil.refreshingMonsters.value) {
                CircularProgressIndicator(color = AppColors.primaryColor)
            } else if (count == owned && count != 0) {
                Image(
                    painter = UiResource.CHECK.imagePainter,
                    "",
                    colorFilter = ColorFilter.tint(AppColors.GREEN),
                    modifier = Modifier.height(15.dp).align(Alignment.CenterVertically).padding(start = 2.dp)
                )
            } else {
                CircularProgressIndicator(
                    color = AppColors.GREEN,
                    progress = if (count == 0) 0f else owned.toFloat() / count.toFloat(),
                    backgroundColor = AppColors.VERY_DARK_BG_COLOR
                )
            }
        }
        CommonText(title, Modifier.fillMaxWidth().weight(1f))
        CommonText("$owned / $count ($missing missing)")
    }
}
