package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

@Composable
fun CharacterHintsContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        val uiState = TreasureHuntUiUtil.getUiStateValue()
        HeaderContent(uiState)
        MapHintsContent(uiState)
        AnimatedVisibility(
            uiState.selectedGfx != null,
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            SelectedHintContent(uiState)
        }
    }
}

@Composable
private fun HeaderContent(uiState: TreasureHuntUiState) {
    val initializedCharacters = CharactersUIUtil.getAllCharacterUIStates()
        .filter { it.activityState == CharacterActivityState.AVAILABLE || it.activityState == CharacterActivityState.BUSY }
        .map { it.name }
    val selectedCharacter = uiState.selectedCharacterName?.takeIf { initializedCharacters.contains(it) }
        ?: initializedCharacters.firstOrNull()
    Column(Modifier.height(60.dp).darkGrayBoxStyle().fillMaxWidth().padding(5.dp)) {
        Row(Modifier.height(30.dp)) {
            CommonText(
                "Load character map GFXs :",
                modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 5.dp)
            )
            Spacer(Modifier.width(10.dp))
            Row(Modifier.align(Alignment.CenterVertically)) {
                if (selectedCharacter != null) {
                    ComboBox(
                        modifier = Modifier.width(150.dp).align(Alignment.CenterVertically),
                        selectedItem = selectedCharacter,
                        items = initializedCharacters,
                        onItemSelect = { TreasureHuntUiUtil.setSelectedCharacterName(it) },
                        getItemText = { it },
                        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = AppColors.backgroundColor)
                    )
                    Spacer(Modifier.width(10.dp))
                    Row(Modifier.align(Alignment.CenterVertically)) {
                        ButtonWithTooltip(
                            onClick = {
                                CharacterManager.getCharacter(selectedCharacter)?.let { character ->
                                    GameSnifferUtil.getFirstConnection(character)?.let { connection ->
                                        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
                                        TreasureHuntUiUtil.loadMapHints(gameInfo)
                                    }
                                }
                            },
                            title = "Load GFXs",
                            imageVector = Icons.Default.Search,
                            shape = RectangleShape,
                        )
                    }
                } else {
                    CommonText(
                        "No initialized character",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Row(Modifier.fillMaxSize()) {
            val loadedStr = uiState.loadedMap?.let {
                val character = uiState.loadedCharacter
                val map = uiState.loadedMap
                "${character?.name} - ${map.coordinates} - ${map.id}"
            } ?: "/"
            CommonText(
                "Loaded : $loadedStr",
                modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 5.dp),
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.MapHintsContent(uiState: TreasureHuntUiState) {
    Box(Modifier.fillMaxSize().padding(start = 5.dp, top = 5.dp, end = 5.dp).weight(1f)) {
        val state = rememberLazyGridState()
        LazyVerticalGrid(columns = GridCells.Adaptive(70.dp), modifier = Modifier.padding(end = 10.dp), state = state) {
            items(uiState.hintsOnMap) { gfxId ->
                val isHovered = remember { mutableStateOf(false) }
                Box(
                    Modifier.fillMaxWidth().height(95.dp).padding(3.dp)
                        .border(BorderStroke(1.dp, Color.Black)).background(AppColors.backgroundColor)
                        .defaultHoverManager(isHovered).handPointerIcon()
                        .onClick { TreasureHuntUiUtil.setSelectedGfx(gfxId) }
                ) {
                    val registeredHints = uiState.hintsGfxByName.filter { it.value.contains(gfxId) }.keys
                    val tooltip = if (registeredHints.size == 1) {
                        registeredHints.firstOrNull() ?: ""
                    } else if (registeredHints.size > 1) {
                        "Registered in ${registeredHints.size} hints"
                    } else ""
                    TooltipTarget(tooltip) {
                        HintGfxCardContent(gfxId, TreasureHuntUiUtil.mapHintsImageCache)
                        if (registeredHints.isNotEmpty()) {
                            val border = BorderStroke(2.dp, AppColors.GREEN.copy(alpha = 0.6f))
                            val backgroundColor = AppColors.GREEN.copy(alpha = 0.1f)
                            Row(modifier = Modifier.fillMaxSize().border(border).background(backgroundColor)) {}
                        }
                        if (uiState.selectedGfx == gfxId) {
                            val border = BorderStroke(4.dp, AppColors.primaryLightColor.copy(alpha = 0.6f))
                            val backgroundColor = AppColors.primaryLightColor.copy(alpha = 0.1f)
                            Row(modifier = Modifier.fillMaxSize().border(border).background(backgroundColor)) {}
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(state),
        )
    }
}

@Composable
private fun SelectedHintContent(uiState: TreasureHuntUiState) {
    val selectedGfx = uiState.selectedGfx
    val character = uiState.loadedCharacter
    val map = uiState.loadedMap
    ExpandedContent(
        title = "Selected GFX - ${character?.name} - ${map?.coordinates}",
        onReduceButtonClick = { TreasureHuntUiUtil.setSelectedGfx(null) },
        key = selectedGfx ?: Unit,
        defaultHeight = 60.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.height(100.dp)) {
            Column(Modifier.width(80.dp).fillMaxHeight()) {
                if (selectedGfx != null) {
                    Box(Modifier.fillMaxWidth().padding(3.dp)) {
                        HintGfxCardContent(selectedGfx, TreasureHuntUiUtil.mapHintsImageCache)
                    }
                }
            }
            Box(
                Modifier.padding(vertical = 8.dp)
                    .border(BorderStroke(1.dp, Color.DarkGray))
                    .width(1.dp)
                    .fillMaxHeight()
            ) {}
            Column(Modifier.width(260.dp).fillMaxHeight().padding(5.dp)) {
                if (character != null) {
                    val characterUiState = CharactersUIUtil.getCharacterUIState(character.name).value
                    val currentHint = characterUiState.currentHintName
                    Column {
                        if (currentHint != null) {
                            CommonText("Current hint : $currentHint", modifier = Modifier.padding(bottom = 10.dp))
                            Row(modifier = Modifier.height(20.dp)) {
                                CommonText("Add GFX to hint :", modifier = Modifier.align(Alignment.CenterVertically))
                                ButtonWithTooltip(
                                    onClick = {
                                        if (selectedGfx != null) {
                                            TreasureHintManager.addHintGfxMatch(currentHint, selectedGfx)
                                            TreasureHuntUiUtil.refreshRegisteredHints()
                                        }
                                    },
                                    title = "Add GFX to hint",
                                    imageVector = Icons.Default.Add,
                                    shape = RectangleShape,
                                    defaultBackgroundColor = AppColors.DARK_BG_COLOR,
                                    hoverBackgroundColor = AppColors.primaryLightColor,
                                    width = 20.dp
                                )
                            }
                        } else {
                            CommonText("No current hint for ${character.name} or hunt isn't initialized")
                        }
                    }
                }
            }
            Box(
                Modifier.padding(vertical = 8.dp)
                    .border(BorderStroke(1.dp, Color.DarkGray))
                    .width(1.dp)
                    .fillMaxHeight()
            ) {}
            Column(Modifier.fillMaxWidth().fillMaxHeight().padding(5.dp)) {
                val registeredHints = uiState.hintsGfxByName.filter { it.value.contains(selectedGfx) }.keys
                if (registeredHints.isNotEmpty()) {
                    CommonText(
                        "Registered in hints :",
                        fontWeight = FontWeight.Bold
                    )
                    for (hint in registeredHints) {
                        Row {
                            CommonText(
                                " - $hint",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    CommonText("Not registered yet", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}