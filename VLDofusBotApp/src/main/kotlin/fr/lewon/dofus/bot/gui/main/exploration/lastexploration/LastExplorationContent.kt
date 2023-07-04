package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Start
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun LastExplorationsContent() {
    val uiState = LastExplorationUiUtil.getUiStateValue()
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle()) {
            CommonText(
                "Last explorations",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(Modifier.padding(5.dp).fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(Modifier.padding(end = 10.dp).verticalScroll(scrollState)) {
                val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
                    .filter { it.activityState != CharacterActivityState.DISCONNECTED }
                for (character in connectedCharactersUIStates) {
                    val lastExploration = uiState.lastExplorationByCharacter[character.name]
                    Column(Modifier.padding(bottom = 20.dp)) {
                        LastExplorationHeaderContent(character.name, lastExploration)
                        Spacer(Modifier.height(3.dp))
                        if (lastExploration == null) {
                            CommonText(
                                "No exploration started yet",
                                modifier = Modifier.padding(start = 30.dp).padding(top = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            val currentSubAreaIndex = lastExploration.subAreasToExplore.indexOfFirst {
                                it.id == lastExploration.currentSubAreaId
                            }
                            for ((index, subArea) in lastExploration.subAreasToExplore.withIndex()) {
                                Row(Modifier.height(20.dp)) {
                                    Row(
                                        Modifier.width(20.dp).height(15.dp).padding(end = 5.dp)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        if (lastExploration.explorationFinished
                                            || currentSubAreaIndex > index
                                            || currentSubAreaIndex == index
                                            && lastExploration.currentAreaProgress == lastExploration.currentAreaTotalCount
                                        ) {
                                            Image(
                                                painter = UiResource.CHECK.imagePainter,
                                                "",
                                                colorFilter = ColorFilter.tint(AppColors.GREEN),
                                                modifier = Modifier.height(15.dp).align(Alignment.CenterVertically)
                                                    .padding(start = 2.dp)
                                            )
                                        } else if (currentSubAreaIndex == index) {
                                            CircularProgressIndicator(
                                                color = if (lastExploration.explorationStopped) AppColors.ORANGE else AppColors.GREEN,
                                                progress = if (lastExploration.currentAreaTotalCount <= 0) 0f else {
                                                    lastExploration.currentAreaProgress.toFloat() / lastExploration.currentAreaTotalCount.toFloat()
                                                },
                                                backgroundColor = AppColors.VERY_DARK_BG_COLOR,
                                            )
                                        } else if (lastExploration.explorationStopped) {
                                            Image(
                                                imageVector = Icons.Default.Close,
                                                "",
                                                colorFilter = ColorFilter.tint(AppColors.RED),
                                                modifier = Modifier.height(15.dp).align(Alignment.CenterVertically)
                                                    .padding(start = 2.dp)
                                            )
                                        }
                                    }
                                    CommonText(
                                        subArea.name,
                                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState),
            )
        }
    }
}

@Composable
private fun LastExplorationHeaderContent(characterName: String, lastExploration: LastExploration?) {
    val characterUIState = CharactersUIUtil.getCharacterUIState(characterName).value
    val character = CharacterManager.getCharacter(characterName)
        ?: error("Character not found : $characterName")
    val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
    Row(Modifier.fillMaxWidth()) {
        Divider(
            Modifier.align(Alignment.CenterVertically).width(30.dp).padding(start = 8.dp, end = 5.dp).height(1.dp)
        )
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
        Spacer(Modifier.width(5.dp))
        TooltipTarget(
            characterUIState.activityState.labelBuilder(character),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Surface(
                shape = CircleShape,
                color = characterUIState.activityState.color,
                modifier = Modifier.size(15.dp)
            ) {}
        }
        Divider(
            Modifier.align(Alignment.CenterVertically).fillMaxWidth().padding(start = 5.dp, end = 8.dp).height(1.dp)
                .weight(1f)
        )
        if (false && lastExploration != null) { //TODO impl buttons
            Row(Modifier.size(20.dp)) {
                val isRestartHovered = mutableStateOf(false)
                ButtonWithTooltip(
                    onClick = {},
                    title = "Restart",
                    imageVector = Icons.Default.RestartAlt,
                    shape = RectangleShape,
                    hoverBackgroundColor = AppColors.primaryLightColor,
                    iconColor = if (isRestartHovered.value) Color.Black else Color.White,
                    isHovered = isRestartHovered,
                    delayMillis = 0
                )
            }
            if (lastExploration.explorationStopped) {
                Spacer(Modifier.width(5.dp))
                Row(Modifier.size(20.dp)) {
                    val isResumeHovered = mutableStateOf(false)
                    ButtonWithTooltip(
                        onClick = {},
                        title = "Resume",
                        imageVector = Icons.Default.Start,
                        shape = RectangleShape,
                        hoverBackgroundColor = AppColors.primaryLightColor,
                        iconColor = if (isResumeHovered.value) Color.Black else Color.White,
                        isHovered = isResumeHovered,
                        delayMillis = 0
                    )
                }
            }
        }
    }

}