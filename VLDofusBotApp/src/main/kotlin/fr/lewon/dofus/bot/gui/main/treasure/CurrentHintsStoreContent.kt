package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.sharp.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager

@Composable
fun CurrentHintsStoreContent() {
    LaunchedEffect(true) {
        TreasureHuntUiUtil.refreshRegisteredHints()
    }
    val uiState = TreasureHuntUiUtil.getUiStateValue()
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        HeaderContent(uiState)
        HintsScrollableBox(uiState.hintsGfxByName, uiState.hintFilter) { hintName, gfxId ->
            DeleteHintOverlayContent(hintName, gfxId, uiState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeleteHintOverlayContent(hintName: String, gfxId: Int, uiState: TreasureHuntUiState) {
    val isHovered = remember { mutableStateOf(false) }
    if (uiState.deleteMode) {
        val borderColor = if (isHovered.value) AppColors.RED else Color.Transparent
        val backgroundColorAlpha = if (isHovered.value) 0.4f else 0.2f
        val iconColorAlpha = if (isHovered.value) 0.5f else 0.3f
        Image(
            Icons.Sharp.Close,
            "",
            modifier = Modifier.fillMaxSize()
                .background(Color.Red.copy(alpha = backgroundColorAlpha))
                .defaultHoverManager(isHovered)
                .border(BorderStroke(2.dp, borderColor))
                .handPointerIcon().onClick {
                    Thread {
                        TreasureHintManager.removeHintGfxMatch(hintName, gfxId)
                        TreasureHuntUiUtil.refreshRegisteredHints()
                    }.start()
                },
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = iconColorAlpha))
        )
    }
}

@Composable
private fun HeaderContent(uiState: TreasureHuntUiState) {
    Column(Modifier.height(60.dp).fillMaxWidth().darkGrayBoxStyle()) {
        Row(Modifier.height(25.dp)) {
            Row {
                val hintsLoading = remember { mutableStateOf(false) }
                RefreshButton(
                    { TreasureHuntUiUtil.refreshRegisteredHints() },
                    "Refresh",
                    CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
                    Color.Gray,
                    width = 40.dp,
                    refreshing = hintsLoading,
                )
            }
            CommonText(
                "Registered hints",
                modifier = Modifier.align(Alignment.CenterVertically).padding(top = 5.dp, start = 10.dp),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.fillMaxWidth().weight(1f))
            ButtonWithTooltip(
                onClick = { TreasureHuntUiUtil.toggleDeleteMode() },
                title = if (uiState.deleteMode) "Exit delete mode" else "Delete mode",
                imageVector = if (uiState.deleteMode) Icons.Default.Close else Icons.Default.Delete,
                shape = CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
                hoverBackgroundColor = if (uiState.deleteMode) AppColors.backgroundColor else AppColors.RED,
                defaultBackgroundColor = if (uiState.deleteMode) AppColors.RED else AppColors.backgroundColor,
                width = 40.dp,
                delayMillis = 0
            )
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 40.dp)) {
            SimpleTextField(
                text = uiState.hintFilter,
                onValueChange = { TreasureHuntUiUtil.updateHintFilter(it) },
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                backgroundColor = AppColors.DARK_BG_COLOR,
                placeHolderText = "Search hint"
            )
        }
    }
}

@Composable
fun HintsScrollableBox(
    gfxIdsByPoiLabel: Map<String, List<Int>>,
    labelFilter: String,
    gfxCardOverlayContent: @Composable BoxScope.(hintName: String, gfxId: Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(5.dp)) {
        val state = rememberScrollState()
        Column(Modifier.fillMaxSize().verticalScroll(state).padding(end = 10.dp)) {
            gfxIdsByPoiLabel.entries.filter {
                StringUtil.removeAccents(it.key).contains(StringUtil.removeAccents(labelFilter))
            }.sortedBy { it.key }.forEach { (hintName, gfxIds) ->
                HintsList(hintName, gfxIds, gfxCardOverlayContent)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(state),
        )
    }
}

@Composable
private fun HintsList(
    hintName: String,
    gfxIds: List<Int>,
    gfxCardOverlayContent: @Composable BoxScope.(hintName: String, gfxId: Int) -> Unit
) {
    HorizontalSeparator(hintName, modifier = Modifier.padding(vertical = 8.dp))
    VerticalGrid(4, gfxIds) { gfxId ->
        val isHovered = remember { mutableStateOf(false) }
        Box(
            Modifier.fillMaxWidth().height(95.dp).padding(3.dp)
                .border(BorderStroke(1.dp, Color.Black)).background(AppColors.backgroundColor)
                .defaultHoverManager(isHovered)
        ) {
            HintGfxCardContent(gfxId, TreasureHuntUiUtil.registeredHintsImageCache)
            gfxCardOverlayContent(hintName, gfxId)
        }
    }
}
