package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.exploration.map.subarea.SubAreaContent
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SelectedSubAreasContent() {
    val mapUiStateValue = ExplorationUIUtil.mapUIState.value
    val selectedSubAreaIds = mapUiStateValue.selectedSubAreaIds
    AnimatedVisibility(
        visible = selectedSubAreaIds.isNotEmpty(),
        modifier = Modifier.onClick { },
        enter = expandHorizontally(expandFrom = Alignment.End),
        exit = shrinkHorizontally(shrinkTowards = Alignment.End)
    ) {
        LaunchedEffect(true) {
            if (MetamobHelperUIUtil.getUiStateValue().metamobMonsters.isEmpty()) {
                Thread { MetamobHelperUIUtil.refreshMonsters() }.start()
            }
        }
        Column(Modifier.width(250.dp).fillMaxHeight().padding(5.dp).grayBoxStyle()) {
            val subAreas = selectedSubAreaIds.map { SubAreaManager.getSubArea(it) }
            ExplorationScriptLauncherContent(subAreas)
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
fun ExplorationScriptLauncherContent(subAreas: List<DofusSubArea>) {
    Column {
        Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle()) {
            CommonText(
                "Explore Area(s) : (${subAreas.size} / ${ExplorationUIUtil.MAX_AREAS_TO_EXPLORE})",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(Modifier.padding(5.dp)) {
            Row {
                Column {
                    for (subArea in subAreas) {
                        CommonText(" - ${subArea.name}", overflow = TextOverflow.Ellipsis, maxLines = 1)
                    }
                }
            }
            HorizontalSeparator(modifier = Modifier.padding(vertical = 5.dp))
            val availableCharacterNames = CharactersUIUtil.getAllCharacterUIStates().map { it.value }.filter {
                it.activityState in listOf(CharacterActivityState.AVAILABLE, CharacterActivityState.TO_INITIALIZE)
            }.map { it.name }
            Row(Modifier.padding(bottom = 5.dp)) {
                CommonText(
                    "Start exploration",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(Modifier.fillMaxWidth().weight(1f))
                Row(Modifier.size(25.dp)) {
                    val enabled = availableCharacterNames.isNotEmpty()
                    ButtonWithTooltip(
                        onClick = { ExplorationUIUtil.startExploration(subAreas) },
                        title = "",
                        shape = RoundedCornerShape(15),
                        hoverBackgroundColor = Color.Gray,
                        defaultBackgroundColor = AppColors.VERY_DARK_BG_COLOR,
                        enabled = enabled
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Image(
                                Icons.Default.PlayArrow,
                                "",
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(if (enabled) AppColors.GREEN else Color.Gray)
                            )
                        }
                    }
                }
            }
            Row(Modifier.padding(bottom = 5.dp)) {
                CommonText("Character used", Modifier.fillMaxWidth(0.5f).align(Alignment.CenterVertically))
                Row(Modifier.fillMaxWidth()) {
                    val selectedCharacterName = ExplorationUIUtil.explorerUIState.value.selectedCharacterName
                        ?.takeIf { it in availableCharacterNames }
                        ?: availableCharacterNames.firstOrNull()
                        ?: ""
                    ExplorationUIUtil.explorerUIState.value =
                        ExplorationUIUtil.explorerUIState.value.copy(selectedCharacterName = selectedCharacterName)
                    Row(Modifier.height(30.dp)) {
                        if (availableCharacterNames.isEmpty()) {
                            CommonText(
                                "No character available",
                                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            ComboBox(
                                Modifier.fillMaxWidth(),
                                selectedItem = selectedCharacterName,
                                items = availableCharacterNames,
                                onItemSelect = {
                                    ExplorationUIUtil.explorerUIState.value =
                                        ExplorationUIUtil.explorerUIState.value.copy(selectedCharacterName = it)
                                },
                                getItemText = { it },
                                maxDropDownHeight = 500.dp
                            ) {
                                if (it.isNotBlank()) {
                                    val breedId = CharactersUIUtil.getCharacterUIState(it).value.dofusClassId
                                    BreedAssetManager.getAssets(breedId).simpleIconPainter
                                } else null
                            }
                        }
                    }
                }
            }
        }
    }
}