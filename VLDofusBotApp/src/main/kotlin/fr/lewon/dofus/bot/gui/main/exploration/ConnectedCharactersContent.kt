package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CommonText
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
fun ConnectedCharactersContent() {
    Column(Modifier.width(200.dp).padding(5.dp).fillMaxHeight()) {
        CommonText("Connected characters", Modifier.padding(bottom = 10.dp), fontWeight = FontWeight.Bold)
        val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
            .filter { it.activityState != CharacterActivityState.DISCONNECTED }
        if (connectedCharactersUIStates.isEmpty()) {
            CommonText("No connected character", Modifier.padding(5.dp))
        } else {
            connectedCharactersUIStates.forEach { uiState ->
                ConnectedCharacterContent(uiState, connectedCharactersUIStates)
            }
        }
    }
}

@Composable
private fun ConnectedCharacterContent(
    characterUIState: CharacterUIState,
    connectedCharacterUIStates: List<CharacterUIState>
) {
    Row(modifier = Modifier.height(30.dp).border(BorderStroke(1.dp, Color.Black))) {
        Row(Modifier.width(6.dp).fillMaxHeight().background(characterUIState.activityState.color)) { }
        Box(Modifier.fillMaxSize().background(Color.DarkGray)) {
            val breedAssets = BreedAssetManager.getAssets(characterUIState.dofusClassId)
            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = breedAssets.simpleIconPainter,
                    "",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.padding(end = 10.dp).align(Alignment.CenterVertically)
                )
                CommonText(
                    characterUIState.name,
                    Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.fillMaxWidth().weight(1f))
                Row(Modifier.fillMaxHeight().padding(3.dp)) {
                    val toGatherCharacters = connectedCharacterUIStates.filter { it.name != characterUIState.name }
                    val currentMapId = characterUIState.currentMap?.id
                    if (characterUIState.activityState != CharacterActivityState.BUSY && currentMapId != null) {
                        val gatherButtonEnabled = toGatherCharacters.isNotEmpty()
                        ButtonWithTooltip(
                            onClick = {
                                toGatherCharacters.forEach {
                                    val character = CharacterManager.getCharacter(it.name)
                                    if (character != null && !ScriptRunner.isScriptRunning(character)) {
                                        ScriptRunner.runScript(
                                            character,
                                            ReachMapScriptBuilder,
                                            ScriptValues().also { scriptValues ->
                                                scriptValues.updateParamValue(
                                                    ReachMapScriptBuilder.reachMapTypeParameter,
                                                    ReachMapScriptBuilder.ReachMapType.BY_MAP_ID.label
                                                )
                                                scriptValues.updateParamValue(
                                                    ReachMapScriptBuilder.mapIdParameter,
                                                    currentMapId.toString()
                                                )
                                            })
                                    }
                                }
                            },
                            title = "Gather available characters",
                            shape = RoundedCornerShape(15),
                            hoverBackgroundColor = Color.Gray,
                            defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                            enabled = gatherButtonEnabled
                        ) {
                            Image(
                                UiResource.GATHER.imagePainter,
                                "",
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(if (gatherButtonEnabled) AppColors.primaryColor else Color.Gray)
                            )
                        }
                    }
                    if (characterUIState.activityState == CharacterActivityState.BUSY) {
                        ButtonWithTooltip(
                            onClick = { ScriptRunner.stopScript(characterUIState.name) },
                            title = "Stop running script",
                            shape = RoundedCornerShape(15),
                            hoverBackgroundColor = Color.Gray,
                            defaultBackgroundColor = AppColors.DARK_BG_COLOR,
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
    }
}