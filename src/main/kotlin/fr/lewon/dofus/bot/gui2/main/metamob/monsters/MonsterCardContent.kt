package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

private val backgroundColor = Color(0xFFf5ecdd)

@Composable
fun MonsterCardContent(monster: MetamobMonster) {
    Column(Modifier.grayBoxStyle().background(backgroundColor)) {
        Box(Modifier.fillMaxSize().weight(1f)) {
            MetamobHelperUIUtil.getPainter(monster)?.let {
                Image(
                    it, "",
                    Modifier.fillMaxHeight().padding(top = 25.dp, end = 5.dp, bottom = 5.dp).align(Alignment.CenterEnd)
                )
            }
            Row(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxWidth().padding(top = 5.dp)) {
                    SelectionContainer {
                        CommonText(monster.name, Modifier.padding(4.dp), enabledColor = Color.Black)
                    }
                    CommonText("Owned : ${monster.amount}", Modifier.padding(4.dp), enabledColor = Color.Black)
                    val status = if (monster.searched > 0) "Searched" else if (monster.offered > 0) "Offered" else "/"
                    CommonText("Status : $status", Modifier.padding(4.dp), enabledColor = Color.Black)
                }
            }
        }
        val color = if (monster.amount > 0) AppColors.GREEN else AppColors.RED
        Row(Modifier.fillMaxWidth().height(6.dp).background(color)) { }
    }
}