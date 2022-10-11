package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.CustomShapes
import fr.lewon.dofus.bot.gui2.custom.RefreshButton
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
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
            LaunchedEffect(Unit) {
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
        val size = remember { mutableStateOf(IntSize(1000, 1000)) }
        val state = rememberScrollState()
        Box(Modifier.fillMaxSize().padding(5.dp)) {
            Column(Modifier.fillMaxSize().padding(end = 8.dp)
                .onGloballyPositioned { size.value = it.size }
                .verticalScroll(state)) {
                if (MetamobHelperUIUtil.getMonsters().isEmpty()) {
                    val errorMessage = MetamobHelperUIUtil.getErrorMessage()
                    if (errorMessage.isNotEmpty()) {
                        CommonText(errorMessage, enabledColor = AppColors.RED)
                    } else {
                        CommonText("No monster to display")
                    }
                } else {
                    val chunkSize = 4
                    val monsterRows = MetamobHelperUIUtil.getFilteredMonsters().chunked(chunkSize)
                    for (monsterRow in monsterRows) {
                        Row(Modifier.fillMaxWidth()) {
                            for (i in 0 until chunkSize) {
                                val monster = monsterRow.getOrNull(i)
                                val boxModifier = if (monster != null) {
                                    Modifier.onGloballyPositioned {
                                        if (it.positionInWindow().y < windowState.size.height.value) {
                                            MetamobHelperUIUtil.loadImagePainter(monster)
                                        }
                                    }
                                } else Modifier
                                Box(boxModifier.fillMaxWidth().height(110.dp).weight(1f).padding(5.dp)) {
                                    if (monster != null) {
                                        MonsterCardContent(monster)
                                    }
                                }
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
}