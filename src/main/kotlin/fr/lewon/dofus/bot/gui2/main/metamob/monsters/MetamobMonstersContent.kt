package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.*
import fr.lewon.dofus.bot.gui2.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.windowState

@Composable
fun MetamobMonstersContent() {
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
        Row {
            val refreshing = remember { mutableStateOf(false) }
            Row(Modifier.height(25.dp)) {
                RefreshButton(
                    { MetamobHelperUIUtil.refreshMonsters() },
                    "Refresh",
                    CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
                    Color.Gray,
                    width = 40.dp,
                    refreshing = refreshing
                )
            }
            LaunchedEffect(true) {
                Thread {
                    try {
                        refreshing.value = true
                        MetamobHelperUIUtil.refreshMonsters()
                    } finally {
                        refreshing.value = false
                    }
                }.start()
            }
            CommonText("Monsters", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
        }
        if (MetamobHelperUIUtil.getMonsters().isEmpty()) {
            val errorMessage = MetamobHelperUIUtil.getErrorMessage()
            Column(Modifier.padding(10.dp)) {
                if (errorMessage.isNotEmpty()) {
                    CommonText(errorMessage, enabledColor = AppColors.RED)
                } else {
                    CommonText("No monster to display")
                }
            }
        } else {
            val state = rememberLazyGridState()
            Box(Modifier.fillMaxSize().padding(5.dp)) {
                val filteredMonsters = MetamobHelperUIUtil.getFilteredMonsters()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    state = state,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    items(filteredMonsters) { monster ->
                        Box(Modifier.onGloballyPositioned {
                            if (it.positionInWindow().y < windowState.size.height.value) {
                                MetamobHelperUIUtil.loadImagePainter(monster)
                            }
                        }.height(110.dp).padding(5.dp)) {
                            MonsterCardContent(monster)
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(state)
                )
            }
        }
    }
}