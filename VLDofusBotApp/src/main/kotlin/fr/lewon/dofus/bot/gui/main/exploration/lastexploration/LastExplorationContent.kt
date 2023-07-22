package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.core.d2o.managers.map.SubAreaManager
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.TooltipPlacement
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

@Composable
fun LastExplorationsContent() {
    val uiState = LastExplorationUiUtil.getUiStateValue()
    Column(Modifier.fillMaxWidth().height(190.dp).padding(horizontal = 5.dp).padding(bottom = 5.dp).grayBoxStyle()) {
        Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle()) {
            CommonText(
                "Last explorations",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
            Row(Modifier.height(25.dp).align(Alignment.CenterVertically)) {
                Spacer(Modifier.width(5.dp))
                val resumeAllEnabled = uiState.lastExplorationByCharacter.values.any {
                    it.explorationStopped && it.getSubAreasToExploreAgain().isNotEmpty()
                }
                ButtonWithTooltip(
                    onClick = {
                        uiState.lastExplorationByCharacter.filter {
                            it.value.explorationStopped && it.value.getSubAreasToExploreAgain().isNotEmpty()
                        }.forEach { (characterName, lastExploration) ->
                            ExplorationUIUtil.startExploration(
                                lastExploration.getSubAreasToExploreAgain(),
                                characterName
                            )
                        }
                    },
                    title = "Resume all explorations",
                    imageVector = Icons.Default.Start,
                    shape = RoundedCornerShape(15),
                    hoverBackgroundColor = Color.Gray,
                    defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                    hoverAnimation = false,
                    delayMillis = 0,
                    width = 25.dp,
                    enabled = resumeAllEnabled,
                    iconColor = if (resumeAllEnabled) Color.White else Color.DarkGray
                )
                Spacer(Modifier.width(5.dp))
                val busyCharacterUiStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }.filter {
                    it.activityState == CharacterActivityState.BUSY
                }
                val stopAllScriptsEnabled = busyCharacterUiStates.isNotEmpty()
                ButtonWithTooltip(
                    onClick = {
                        busyCharacterUiStates.forEach { characterUiState ->
                            ScriptRunner.stopScript(characterUiState.name)
                        }
                    },
                    title = "Stop all running scripts",
                    imageVector = Icons.Default.Stop,
                    shape = RoundedCornerShape(15),
                    hoverBackgroundColor = Color.Gray,
                    defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                    delayMillis = 0,
                    width = 25.dp,
                    enabled = stopAllScriptsEnabled,
                    iconColor = if (stopAllScriptsEnabled) AppColors.RED else Color.DarkGray
                )
            }
        }
        Row(Modifier.padding(5.dp).fillMaxSize()) {
            val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
                .filter { it.activityState != CharacterActivityState.DISCONNECTED }
            for (characterUiState in connectedCharactersUIStates) {
                val character = CharacterManager.getCharacter(characterUiState.name)
                    ?: error("Character not found : ${characterUiState.name}")
                val lastExploration = uiState.lastExplorationByCharacter[characterUiState.name]
                Row(Modifier.width(190.dp).padding(horizontal = 5.dp).darkGrayBoxStyle()) {
                    TooltipTarget(
                        text = characterUiState.activityState.labelBuilder(character),
                        tooltipPlacement = TooltipPlacement.Top,
                        delayMillis = 1000
                    ) {
                        Row(Modifier.width(5.dp).fillMaxHeight().background(characterUiState.activityState.color)) {}
                    }
                    Column(Modifier.padding(horizontal = 5.dp).padding(bottom = 5.dp)) {
                        LastExplorationHeaderContent(characterUiState.name, lastExploration)
                        Spacer(Modifier.height(3.dp))
                        if (lastExploration == null) {
                            CommonText(
                                "No exploration started yet",
                                modifier = Modifier.padding(start = 30.dp).padding(top = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            for ((subArea, explorationProgress) in lastExploration.progressBySubArea) {
                                Row(Modifier.height(20.dp).padding(start = 6.dp)) {
                                    Row(
                                        Modifier.width(20.dp).height(15.dp).padding(end = 5.dp)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        if (explorationProgress.started && explorationProgress.current >= explorationProgress.total) {
                                            Image(
                                                painter = UiResource.CHECK.imagePainter,
                                                "",
                                                colorFilter = ColorFilter.tint(AppColors.GREEN),
                                                modifier = Modifier.height(15.dp).align(Alignment.CenterVertically)
                                                    .padding(start = 2.dp)
                                            )
                                        } else if (lastExploration.explorationStopped && explorationProgress.current == 0) {
                                            Image(
                                                imageVector = Icons.Default.Close,
                                                "",
                                                colorFilter = ColorFilter.tint(AppColors.RED),
                                                modifier = Modifier.height(15.dp).align(Alignment.CenterVertically)
                                                    .padding(start = 2.dp)
                                            )
                                        } else if (explorationProgress.started) {
                                            CircularProgressIndicator(
                                                color = if (lastExploration.explorationStopped) AppColors.ORANGE else AppColors.GREEN,
                                                progress = if (explorationProgress.total > 0) {
                                                    explorationProgress.current.toFloat() / explorationProgress.total.toFloat()
                                                } else 0f,
                                                backgroundColor = AppColors.VERY_DARK_BG_COLOR,
                                            )
                                        }
                                    }
                                    CommonText(
                                        subArea.name,
                                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 5.dp),
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LastExplorationHeaderContent(characterName: String, lastExploration: LastExploration?) {
    val characterUIState = CharactersUIUtil.getCharacterUIState(characterName).value
    val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
    Row(Modifier.fillMaxWidth().padding(top = 2.dp)) {
        Image(
            painter = breedAssets.simpleIconPainter,
            "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.height(25.dp)
        )
        Spacer(Modifier.width(5.dp))
        CommonText(
            characterName,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(Modifier.fillMaxWidth().weight(1f))
        val buttonSize = 25.dp
        if (lastExploration != null) {
            val toExploreAgain = lastExploration.getSubAreasToExploreAgain()
            if (characterUIState.activityState != CharacterActivityState.BUSY && toExploreAgain.isNotEmpty()) {
                Row(Modifier.size(buttonSize).align(Alignment.CenterVertically)) {
                    ButtonWithTooltip(
                        onClick = { ExplorationUIUtil.startExploration(toExploreAgain, characterName) },
                        title = "Resume",
                        imageVector = Icons.Default.Start,
                        shape = RoundedCornerShape(15),
                        hoverBackgroundColor = Color.Gray,
                        defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                        hoverAnimation = false,
                        delayMillis = 0,
                        width = buttonSize
                    )
                }
            }
        }
        if (characterUIState.activityState != CharacterActivityState.BUSY) {
            val subAreas = ExplorationUIUtil.mapUIState.value.selectedSubAreaIds.map(SubAreaManager::getSubArea)
            Row(Modifier.size(buttonSize).align(Alignment.CenterVertically)) {
                ButtonWithTooltip(
                    onClick = {
                        ExplorationUIUtil.startExploration(subAreas, characterName)
                        ExplorationUIUtil.mapUIState.value = ExplorationUIUtil.mapUIState.value.copy(
                            selectedSubAreaIds = emptyList()
                        )
                    },
                    title = "Start exploration",
                    shape = RoundedCornerShape(15),
                    enabled = subAreas.isNotEmpty(),
                    hoverBackgroundColor = Color.Gray,
                    defaultBackgroundColor = AppColors.VERY_DARK_BG_COLOR,
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            Icons.Default.PlayArrow,
                            "",
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(if (subAreas.isNotEmpty()) AppColors.GREEN else Color.Gray)
                        )
                    }
                }
            }
        }
        if (characterUIState.activityState == CharacterActivityState.BUSY) {
            Row(Modifier.size(buttonSize).align(Alignment.CenterVertically)) {
                ButtonWithTooltip(
                    onClick = { ScriptRunner.stopScript(characterUIState.name) },
                    title = "Stop running script",
                    shape = RoundedCornerShape(15),
                    hoverBackgroundColor = Color.Gray,
                    defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                    delayMillis = 0,
                    width = buttonSize
                ) {
                    Image(
                        Icons.Default.Stop,
                        "",
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(AppColors.RED)
                    )
                }
            }
        }
    }

}