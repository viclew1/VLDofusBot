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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.exploration.map.subarea.SubAreaContent
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SelectedSubAreaContent() {
    val selectedSubAreaId = ExplorationUIUtil.mapUIState.value.selectedMapDrawCell?.subAreaId
    val subAreaState = remember { mutableStateOf<DofusSubArea?>(null) }
    AnimatedVisibility(
        visible = selectedSubAreaId != null,
        modifier = Modifier.onClick { },
        enter = expandHorizontally(expandFrom = Alignment.End),
        exit = shrinkHorizontally(shrinkTowards = Alignment.End)
    ) {
        LaunchedEffect(true) {
            if (MetamobHelperUIUtil.uiState.value.metamobMonsters.isEmpty()) {
                Thread { MetamobHelperUIUtil.refreshMonsters() }.start()
            }
        }
        Column(Modifier.width(320.dp).fillMaxHeight().grayBoxStyle().padding(10.dp)) {
            selectedSubAreaId?.let { subAreaState.value = SubAreaManager.getSubArea(it) }
            val subArea = subAreaState.value
            if (subArea != null) {
                ExplorationScriptLauncherContent(subArea)
                Spacer(Modifier.height(5.dp))
                SubAreaContent(subArea)
            }
        }
    }
}

@Composable
fun ExplorationScriptLauncherContent(subArea: DofusSubArea) {
    Column(Modifier.fillMaxWidth().darkGrayBoxStyle().padding(5.dp)) {
        CommonText("Explore Area", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
        Row(Modifier.padding(start = 5.dp)) {
            Column {
                CommonText("Area", fontWeight = FontWeight.Bold)
                CommonText("Sub Area", fontWeight = FontWeight.Bold)
            }
            Column {
                CommonText(" : ${subArea.area.name}", overflow = TextOverflow.Ellipsis, maxLines = 1)
                CommonText(" : ${subArea.name}", overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
        }
        HorizontalSeparator(modifier = Modifier.padding(vertical = 10.dp))
        if (ExplorationUIUtil.explorerUIState.value.availableCharacters.isEmpty()) {
            CommonText(
                "No character available to explore this area",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
            )
        } else {
            Row(Modifier.padding(bottom = 5.dp)) {
                CommonText(
                    "Start exploration",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(Modifier.fillMaxWidth().weight(1f))
                Row(Modifier.size(25.dp)) {
                    ButtonWithTooltip(
                        onClick = { ExplorationUIUtil.startExploration(subArea) },
                        title = "",
                        shape = RoundedCornerShape(15),
                        hoverBackgroundColor = Color.Gray,
                        defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Image(
                                Icons.Default.PlayArrow,
                                "",
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(AppColors.GREEN)
                            )
                        }
                    }
                }
            }
            Row(Modifier.padding(bottom = 5.dp)) {
                CommonText("Character used", Modifier.fillMaxWidth(0.5f).align(Alignment.CenterVertically))
                Row(Modifier.fillMaxWidth()) {
                    ComboBox(
                        Modifier.fillMaxWidth(),
                        selectedItem = ExplorationUIUtil.explorerUIState.value.selectedCharacterName ?: "",
                        items = ExplorationUIUtil.explorerUIState.value.availableCharacters,
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
            HorizontalSeparator()
            for ((parameter, value) in ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByParameter) {
                Row(Modifier.padding(vertical = 5.dp)) {
                    val widthRatio = if (parameter.type == DofusBotParameterType.CHOICE) 0.5f else 0.7f
                    Column(Modifier.fillMaxWidth(widthRatio).align(Alignment.CenterVertically)) {
                        CommonText(parameter.key)
                    }
                    Spacer(Modifier.width(10.dp))
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                    ParameterInput(
                        Modifier,
                        parameter,
                        getParamValue = { value },
                        onParamUpdate = {
                            ExplorationUIUtil.explorerUIState.value = ExplorationUIUtil.explorerUIState.value.copy(
                                explorationParameterValuesByParameter = ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByParameter
                                    .plus(parameter to it)
                            )
                        }
                    )
                }
            }
        }
    }
}