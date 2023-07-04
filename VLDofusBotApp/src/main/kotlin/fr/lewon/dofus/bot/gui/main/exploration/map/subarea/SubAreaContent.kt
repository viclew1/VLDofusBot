package fr.lewon.dofus.bot.gui.main.exploration.map.subarea

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.CustomShapes
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun ColumnScope.SubAreaContent(subArea: DofusSubArea) {
    val currentSubAreaContentTabValue = ExplorationUIUtil.currentSubAreaContentTab.value
    Column(Modifier.fillMaxSize().weight(1f)) {
        Row(Modifier.height(30.dp)) {
            for (subAreaContentTab in SubAreaContentTabs.values()) {
                val backgroundColor =
                    if (subAreaContentTab == currentSubAreaContentTabValue) Color.Gray else AppColors.backgroundColor
                Box {
                    ButtonWithTooltip(
                        { ExplorationUIUtil.currentSubAreaContentTab.value = subAreaContentTab },
                        subAreaContentTab.title,
                        subAreaContentTab.resource.imagePainter,
                        CustomShapes.buildTrapezoidShape(),
                        Color.LightGray,
                        backgroundColor
                    )
                }
                Divider(Modifier.fillMaxHeight().width(2.dp))
            }
            CommonText(
                currentSubAreaContentTabValue.title,
                Modifier.padding(start = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Divider(Modifier.fillMaxWidth().padding(end = 8.dp).height(1.dp))
        Row(Modifier.fillMaxSize().darkGrayBoxStyle()) {
            currentSubAreaContentTabValue.content(subArea)
        }
    }
}