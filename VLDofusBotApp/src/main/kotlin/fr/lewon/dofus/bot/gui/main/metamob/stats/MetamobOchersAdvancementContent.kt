package fr.lewon.dofus.bot.gui.main.metamob.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager

@Composable
fun MetamobOchersAdvancementContent() {
    val simultaneousOchers = MetamobConfigManager.readConfig().getSafeSimultaneousOchers()
    var ocherIndex by remember { mutableStateOf(1) }
    Column(Modifier.padding(5.dp).grayBoxStyle()) {
        Row(Modifier.height(30.dp).darkGrayBoxStyle()) {
            Row(Modifier.width(30.dp)) {
                if (ocherIndex > 1) {
                    ButtonWithTooltip(
                        onClick = { ocherIndex -= 1 },
                        title = "",
                        imageVector = Icons.Default.ChevronLeft,
                        shape = CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
                        hoverBackgroundColor = AppColors.primaryLightColor,
                        width = 30.dp
                    )
                }
            }
            CommonText(
                "Ocher $ocherIndex",
                Modifier.align(Alignment.CenterVertically).fillMaxWidth().weight(1f),
                textAlign = TextAlign.Center
            )
            Row(Modifier.width(30.dp)) {
                if (ocherIndex < simultaneousOchers) {
                    ButtonWithTooltip(
                        onClick = { ocherIndex += 1 },
                        title = "",
                        imageVector = Icons.Default.ChevronRight,
                        shape = CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
                        hoverBackgroundColor = AppColors.primaryLightColor,
                        width = 30.dp,
                    )
                }
            }
        }
        Row(Modifier.padding(5.dp)) {
            MetamobStatsContent(ocherIndex)
        }
    }
}