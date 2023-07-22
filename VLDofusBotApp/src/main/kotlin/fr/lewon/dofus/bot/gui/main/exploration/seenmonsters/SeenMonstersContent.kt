package fr.lewon.dofus.bot.gui.main.exploration.seenmonsters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2p.gfx.D2PMonstersGfxAdapter
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.gui.util.getBufferedImage
import fr.lewon.dofus.bot.gui.util.trimImage
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.impl.ReachMapScriptBuilder
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Composable
fun SeenMonstersContent() {
    val uiState = SeenMonstersUiUtil.getUiStateValue()
    val connectedCharactersUIStates = CharactersUIUtil.getAllCharacterUIStates().map { it.value }
        .filter { it.activityState != CharacterActivityState.DISCONNECTED }
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        Row(Modifier.height(30.dp).fillMaxWidth().darkGrayBoxStyle()) {
            CommonText(
                "Seen monsters",
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(Modifier.fillMaxSize().padding(5.dp)) {
            val scrollState = rememberScrollState()
            val seenMonsters = uiState.seenMonstersByMap.flatMap { it.value }
                .sortedByDescending { it.time }
            Column(Modifier.fillMaxSize().padding(end = 10.dp).verticalScroll(scrollState)) {
                for (seenMonster in seenMonsters) {
                    SeenMonsterContent(seenMonster, connectedCharactersUIStates)
                    Spacer(Modifier.height(5.dp))
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
private fun SeenMonsterContent(seenMonster: SeenMonster, connectedCharactersUIStates: List<CharacterUIState>) {
    val isHovered = remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth().darkGrayBoxStyle().defaultHoverManager(isHovered)) {
        val monsterPainter = D2PMonstersGfxAdapter.getMonsterImageData(seenMonster.monster.id)
            .getBufferedImage().trimImage().toPainter()
        Box(Modifier.fillMaxWidth().padding(5.dp)) {
            Image(
                monsterPainter,
                "",
                modifier = Modifier.align(Alignment.BottomEnd).padding(top = 20.dp, end = 20.dp).width(50.dp)
            )
            seenMonster.type.iconPainter?.let { painter ->
                Image(
                    painter,
                    "",
                    modifier = Modifier.size(30.dp).align(Alignment.BottomEnd),
                )
            }
            Row {
                Column(Modifier.fillMaxWidth().weight(1f)) {
                    val instant = Instant.ofEpochMilli(seenMonster.time)
                    val ldt = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
                    SelectionContainer {
                        Column {
                            CommonText(
                                seenMonster.monster.name,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.height(20.dp)
                            )
                            CommonText(
                                "Seen at : ${FormatUtil.localDateTimeToStr(ldt)}",
                                modifier = Modifier.height(20.dp)
                            )
                            CommonText(
                                "Coordinates : ${seenMonster.map.coordinates}",
                                modifier = Modifier.height(20.dp)
                            )
                            CommonText("Map ID : ${seenMonster.map.id.toLong()}", modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
        if (isHovered.value) {
            Row(Modifier.height(25.dp)) {
                ButtonWithTooltip(
                    onClick = {
                        SeenMonstersUiUtil.removeSeenMonster(seenMonster)
                    },
                    title = "Remove",
                    imageVector = Icons.Default.Close,
                    shape = CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.2f),
                    width = 40.dp,
                    hoverBackgroundColor = AppColors.RED,
                    delayMillis = 0
                )
            }
        }
        val gatherButtonEnabled = connectedCharactersUIStates.isNotEmpty()
        Row(Modifier.height(25.dp).align(Alignment.TopEnd)) {
            ButtonWithTooltip(
                onClick = {
                    connectedCharactersUIStates.filter { it.currentMap != seenMonster.map }.forEach {
                        val toGatherCharacter = CharacterManager.getCharacter(it.name)
                        if (toGatherCharacter != null && !ScriptRunner.isScriptRunning(toGatherCharacter)) {
                            ScriptRunner.runScript(
                                toGatherCharacter,
                                ReachMapScriptBuilder,
                                ParameterValues().also { parameterValues ->
                                    parameterValues.updateParamValue(
                                        ReachMapScriptBuilder.reachMapTypeParameter,
                                        ReachMapScriptBuilder.ReachMapType.BY_MAP_ID
                                    )
                                    parameterValues.updateParamValue(
                                        ReachMapScriptBuilder.mapIdParameter,
                                        seenMonster.map.id.toLong()
                                    )
                                })
                        }
                    }
                },
                title = "Gather available characters",
                shape = CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.2f),
                hoverBackgroundColor = Color.Gray,
                enabled = gatherButtonEnabled,
                delayMillis = 0,
                width = 40.dp
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
}
