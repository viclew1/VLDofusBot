package fr.lewon.dofus.bot.gui.main.exploration.lastexploration

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.impl.ReachMapScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

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
                        LastExplorationHeaderContent(connectedCharactersUIStates, character.name, lastExploration)
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
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState),
            )
        }
    }
}

@Composable
private fun LastExplorationHeaderContent(
    connectedCharacterUIStates: List<CharacterUIState>,
    characterName: String,
    lastExploration: LastExploration?
) {
    val characterUIState = CharactersUIUtil.getCharacterUIState(characterName).value
    val character = CharacterManager.getCharacter(characterName)
        ?: error("Character not found : $characterName")
    val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
    Row(Modifier.fillMaxWidth()) {
        TooltipTarget(
            characterUIState.activityState.labelBuilder(character),
            modifier = Modifier.align(Alignment.CenterVertically).padding(end = 5.dp, start = 6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = characterUIState.activityState.color,
                modifier = Modifier.size(15.dp)
            ) {}
        }
        Spacer(Modifier.width(5.dp))
        CommonText(
            characterName,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(5.dp))
        Image(
            painter = breedAssets.simpleIconPainter,
            "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.height(25.dp)
        )
        Divider(
            Modifier.align(Alignment.CenterVertically).fillMaxWidth().padding(start = 5.dp, end = 5.dp).height(1.dp)
                .weight(1f)
        )
        val buttonSize = 25.dp
        if (lastExploration != null) {
            val toExploreAgain = lastExploration.progressBySubArea.filter {
                it.value.total == 0 || it.value.current < it.value.total
            }.keys.toList()
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
        if (characterUIState.activityState != CharacterActivityState.BUSY && characterUIState.currentMap != null) {
            val toGatherCharacters = connectedCharacterUIStates.filter { it.name != characterUIState.name }
            val gatherButtonEnabled = toGatherCharacters.isNotEmpty()
            Row(Modifier.size(buttonSize).align(Alignment.CenterVertically)) {
                ButtonWithTooltip(
                    onClick = {
                        toGatherCharacters.forEach {
                            val toGatherCharacter = CharacterManager.getCharacter(it.name)
                            if (toGatherCharacter != null && !ScriptRunner.isScriptRunning(toGatherCharacter)) {
                                ScriptRunner.runScript(
                                    toGatherCharacter,
                                    ReachMapScriptBuilder,
                                    ScriptValues().also { scriptValues ->
                                        scriptValues.updateParamValue(
                                            ReachMapScriptBuilder.reachMapTypeParameter,
                                            ReachMapScriptBuilder.ReachMapType.BY_MAP_ID.label
                                        )
                                        scriptValues.updateParamValue(
                                            ReachMapScriptBuilder.mapIdParameter,
                                            characterUIState.currentMap.id.toString()
                                        )
                                    })
                            }
                        }
                    },
                    title = "Gather available characters",
                    shape = RoundedCornerShape(15),
                    hoverBackgroundColor = Color.Gray,
                    defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                    enabled = gatherButtonEnabled,
                    delayMillis = 0,
                    width = buttonSize
                ) {
                    Image(
                        UiResource.GATHER.imagePainter,
                        "",
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(if (gatherButtonEnabled) AppColors.primaryColor else Color.Gray)
                    )
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