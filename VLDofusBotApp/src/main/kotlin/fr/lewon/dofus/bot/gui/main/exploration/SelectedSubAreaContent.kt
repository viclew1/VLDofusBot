package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.exploration.map.subarea.SubAreaContent
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun SelectedSubAreasContent() {
    val mapUiStateValue = ExplorationUIUtil.mapUIState.value
    val selectedSubAreaIds = mapUiStateValue.selectedSubAreaIds
    LaunchedEffect(true) {
        if (MetamobHelperUIUtil.getUiStateValue().metamobMonsters.isEmpty()) {
            Thread { MetamobHelperUIUtil.refreshMonsters() }.start()
        }
    }
    Column(Modifier.width(250.dp).fillMaxHeight().padding(5.dp).padding(bottom = 5.dp).grayBoxStyle()) {
        val subAreas = selectedSubAreaIds.map { SubAreaManager.getSubArea(it) }
        HeaderContent(subAreas)
        if (subAreas.isNotEmpty()) {
            val subAreaIndex = mapUiStateValue.selectedSubAreaIndex
            val subArea = subAreas.getOrNull(subAreaIndex)
            Column(Modifier.grayBoxStyle()) {
                Row(Modifier.height(40.dp).darkGrayBoxStyle()) {
                    Row(Modifier.width(30.dp)) {
                        if (subAreaIndex > 0) {
                            ButtonWithTooltip(
                                onClick = {
                                    ExplorationUIUtil.mapUIState.value = mapUiStateValue.copy(
                                        selectedSubAreaIndex = subAreaIndex - 1
                                    )
                                },
                                title = "",
                                imageVector = Icons.Default.ChevronLeft,
                                shape = CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
                                hoverBackgroundColor = AppColors.primaryLightColor,
                                width = 30.dp
                            )
                        }
                    }
                    if (subArea != null) {
                        CommonText(
                            subArea.name,
                            Modifier.align(Alignment.CenterVertically).fillMaxWidth().weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(Modifier.width(30.dp)) {
                        if (subAreaIndex < subAreas.size - 1) {
                            ButtonWithTooltip(
                                onClick = {
                                    ExplorationUIUtil.mapUIState.value = mapUiStateValue.copy(
                                        selectedSubAreaIndex = subAreaIndex + 1
                                    )
                                },
                                title = "",
                                imageVector = Icons.Default.ChevronRight,
                                shape = CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
                                hoverBackgroundColor = AppColors.primaryLightColor,
                                width = 30.dp,
                            )
                        }
                    }
                }
                if (subArea != null) {
                    SubAreaContent(subArea)
                }
            }
        }
    }
}

@Composable
fun HeaderContent(subAreas: List<DofusSubArea>) {
    CustomStyledColumn("Selected Area(s) : (${subAreas.size} / ${ExplorationUIUtil.MinAreasToExplore})") {
        Column(Modifier.padding(5.dp)) {
            if (subAreas.isEmpty()) {
                CommonText("No area selected", modifier = Modifier.fillMaxHeight().padding(start = 10.dp))
            } else {
                for (subArea in subAreas) {
                    CommonText(" - ${subArea.name}", overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
        }
    }
}