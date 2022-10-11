package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.CustomShapes
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CharacterEditionTabsContent(characterUIState: CharacterUIState) {
    val currentEditionTab = CharacterEditionUIUtil.getEditionTab()
    Column(Modifier.fillMaxSize().padding(top = 5.dp, end = 5.dp).grayBoxStyle()) {
        Column(Modifier.fillMaxSize().weight(1f)) {
            Row(Modifier.height(30.dp)) {
                for (editionTab in EditionTab.values()) {
                    val backgroundColor =
                        if (editionTab == currentEditionTab) Color.Gray else AppColors.backgroundColor
                    Box {
                        ButtonWithTooltip(
                            { CharacterEditionUIUtil.updateEditionTab(editionTab) },
                            editionTab.title,
                            editionTab.resource.imagePainter,
                            CustomShapes.buildTrapezoidShape(),
                            Color.LightGray,
                            backgroundColor
                        )
                    }
                    Divider(Modifier.fillMaxHeight().width(2.dp))
                }
                CommonText(
                    currentEditionTab.title,
                    Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Divider(Modifier.fillMaxWidth().padding(end = 8.dp).height(1.dp))
            currentEditionTab.content(characterUIState)
        }
    }
}