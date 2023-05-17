package fr.lewon.dofus.bot.gui.main.exploration.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.impl.ExploreAreaScriptBuilder
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SelectedSubAreaContent() {
    val selectedSubAreaId = ExplorationUIUtil.mapUIState.value.selectedSubAreaId
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
        Column(Modifier.width(300.dp).fillMaxHeight().grayBoxStyle().padding(10.dp)) {
            selectedSubAreaId?.let { subAreaState.value = SubAreaManager.getSubArea(it) }
            val subArea = subAreaState.value
            if (subArea != null) {
                ExplorationScriptLauncherContent(subArea)
                Spacer(Modifier.height(5.dp))
                SubAreaMonstersContent(subArea)
            }
        }
    }
}

@Composable
private fun SubAreaMonstersContent(subArea: DofusSubArea) {
    Box(Modifier.fillMaxSize().darkGrayBoxStyle()) {
        val state = rememberScrollState()
        Column(Modifier.fillMaxSize().padding(5.dp).padding(end = 8.dp).verticalScroll(state)) {
            if (subArea.monsters.isEmpty()) {
                CommonText(
                    "No monster in this sub area",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
                )
            } else {
                MonsterListContent("Monsters", subArea.monsters.filter { !it.isMiniBoss && !it.isQuestMonster })
                MonsterListContent("Archmonsters", subArea.monsters.filter { it.isMiniBoss })
                MonsterListContent("Quest monsters", subArea.monsters.filter { it.isQuestMonster })
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                .background(AppColors.backgroundColor),
            adapter = rememberScrollbarAdapter(state),
        )
    }
}

@Composable
private fun MonsterListContent(title: String, monsters: List<DofusMonster>) {
    if (monsters.isNotEmpty()) {
        HorizontalSeparator(title, Modifier.padding(vertical = 10.dp))
        for (monster in monsters) {
            Row {
                OwnedIndicatorContent(monster)
                Spacer(Modifier.width(5.dp))
                CommonText(monster.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun RowScope.OwnedIndicatorContent(monster: DofusMonster) {
    if (MetamobHelperUIUtil.refreshingMonsters.value) {
        Box(Modifier.requiredSize(8.dp).align(Alignment.CenterVertically)) {
            CircularProgressIndicator(Modifier.fillMaxSize(), color = AppColors.primaryColor)
        }
    } else {
        val allMetamobMonsters = MetamobHelperUIUtil.uiState.value.metamobMonsters
        val metamobMonster = MetamobMonstersHelper.getMetamobMonster(monster, allMetamobMonsters)
        val color = when {
            metamobMonster == null -> Color.White
            metamobMonster.amount > 0 -> AppColors.GREEN
            else -> AppColors.RED
        }
        CommonText(metamobMonster?.amount?.toString() ?: "/", enabledColor = color)
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
                        onClick = {
                            ExplorationUIUtil.explorerUIState.value.selectedCharacterName?.let { characterName ->
                                CharacterManager.getCharacter(characterName)?.let { character ->
                                    val scriptValues = ScriptValues()
                                    ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByName.forEach {
                                        scriptValues.updateParamValue(it.key, it.value)
                                    }
                                    scriptValues.updateParamValue(
                                        ExploreAreaScriptBuilder.subAreaParameter,
                                        subArea.label
                                    )
                                    ScriptRunner.runScript(character, ExploreAreaScriptBuilder, scriptValues)
                                }
                            }
                        },
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
                ComboBox(
                    Modifier.fillMaxWidth(),
                    selectedItem = ExplorationUIUtil.explorerUIState.value.selectedCharacterName ?: "",
                    items = ExplorationUIUtil.explorerUIState.value.availableCharacters,
                    onItemSelect = {
                        ExplorationUIUtil.explorerUIState.value =
                            ExplorationUIUtil.explorerUIState.value.copy(selectedCharacterName = it)
                    },
                    getItemText = { it },
                    maxDropDownHeight = 500.dp,
                    getItemIconPainter = {
                        if (it.isNotBlank()) {
                            val breedId = CharactersUIUtil.getCharacterUIState(it).value.dofusClassId
                            BreedAssetManager.getAssets(breedId).simpleIconPainter
                        } else null
                    }
                )
            }
            HorizontalSeparator()
            for ((parameter, value) in ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByName) {
                Row {
                    Column(Modifier.fillMaxWidth(0.7f).align(Alignment.CenterVertically)) {
                        CommonText(parameter.key)
                    }
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                    Row(Modifier.align(Alignment.CenterVertically)) {
                        ParameterInput(
                            Modifier,
                            parameter,
                            getParamValue = { value },
                            onParamUpdate = {
                                ExplorationUIUtil.explorerUIState.value = ExplorationUIUtil.explorerUIState.value.copy(
                                    explorationParameterValuesByName = ExplorationUIUtil.explorerUIState.value.explorationParameterValuesByName
                                        .plus(parameter to it)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}