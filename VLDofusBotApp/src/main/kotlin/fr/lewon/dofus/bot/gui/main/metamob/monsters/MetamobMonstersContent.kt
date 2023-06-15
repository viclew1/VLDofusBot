package fr.lewon.dofus.bot.gui.main.metamob.monsters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.DropTarget
import fr.lewon.dofus.bot.gui.main.dragTargetInfo
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun MetamobMonstersContent() {
    Row {
        MetamobMonstersListContent()
        MetamobTradeHelperContent()
    }
}

@Composable
private fun MetamobTradeHelperContent() {
    val tradeUiState = MetamobTradeUIUtil.getUiStateValue()
    Row(Modifier.fillMaxHeight().padding(end = 5.dp, top = 5.dp, bottom = 5.dp).grayBoxStyle()) {
        val isButtonHovered = remember { mutableStateOf(false) }
        val tradeOpened = tradeUiState.tradeOpened
        ButtonWithTooltip(
            { MetamobTradeUIUtil.setTradeOpened(!tradeOpened) },
            title = if (tradeOpened) "Reduce" else "Expand",
            imageVector = if (tradeOpened) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
            shape = RectangleShape,
            hoverBackgroundColor = AppColors.primaryLightColor,
            iconColor = if (isButtonHovered.value) Color.Black else Color.White,
            isHovered = isButtonHovered,
            width = 30.dp
        )
        AnimatedVisibility(
            tradeOpened,
            enter = expandHorizontally(expandFrom = Alignment.End),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End)
        ) {
            Column(Modifier.fillMaxHeight().width(250.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    CommonText("Trade helper", modifier = Modifier.padding(5.dp), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                    AnimatedButton(
                        { MetamobTradeUIUtil.clearTrade() },
                        "Clear",
                        Icons.Default.Clear,
                        Modifier.height(25.dp).width(80.dp),
                        shape = CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
                        iconColor = Color.White
                    )
                }
                Column(Modifier.fillMaxSize().padding(5.dp).weight(1f).grayBoxStyle()) {
                    CommonText("My monsters", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
                    TradeArea(tradeUiState.playerTradeMonsters) {
                        MetamobTradeUIUtil.updateTrade(it, tradeUiState.otherGuyTradeMonsters)
                    }
                }
                Column(Modifier.fillMaxSize().padding(5.dp).weight(1f).grayBoxStyle()) {
                    CommonText("His monsters", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.SemiBold)
                    TradeArea(tradeUiState.otherGuyTradeMonsters) {
                        MetamobTradeUIUtil.updateTrade(tradeUiState.playerTradeMonsters, it)
                    }
                }
                val isCopyButtonHovered = remember { mutableStateOf(false) }
                Row(Modifier.fillMaxWidth().height(30.dp).padding(5.dp)) {
                    ButtonWithTooltip(
                        { copyOffer(tradeUiState) },
                        "",
                        Icons.Default.ContentCopy,
                        RectangleShape,
                        width = 30.dp,
                        hoverBackgroundColor = AppColors.primaryLightColor,
                        iconColor = if (isCopyButtonHovered.value) Color.Black else Color.White,
                        isHovered = isCopyButtonHovered,
                    )
                    CommonText(
                        "Copy offer in clipboard",
                        Modifier.padding(start = 5.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

private fun copyOffer(tradeUiState: MetamobTradeUiState) {
    val allMonsters = MetamobHelperUIUtil.getUiStateValue().metamobMonsters
    val myMonstersStr = tradeUiState.playerTradeMonsters.joinToString(", ") { getMonsterStr(it, allMonsters) }
    val otherGuyMonstersStr = tradeUiState.otherGuyTradeMonsters.joinToString(", ") { getMonsterStr(it, allMonsters) }
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val clipboardContent = StringSelection("$myMonstersStr VS $otherGuyMonstersStr")
    clipboard.setContents(clipboardContent, clipboardContent)
}

private fun getMonsterStr(monster: MetamobMonster, allMonsters: List<MetamobMonster>): String {
    val price = MetamobHelperUIUtil.getPrice(monster) ?: 0L
    val monsterDisplayName = monster.name.split(" ").firstOrNull()
        ?.takeIf { name -> allMonsters.none { it.id != monster.id && it.name.startsWith(name) } }
        ?: monster.name
    return "$monsterDisplayName ${price / 1000}k"
}

@Composable
fun TradeArea(
    tradeMonsters: List<MetamobMonster>,
    onListUpdate: (List<MetamobMonster>) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val total = tradeMonsters.sumOf { MetamobHelperUIUtil.getPrice(it) ?: 0L }
        SelectionContainer {
            CommonText(
                "Total : ${"%,d".format(total)} K (${tradeMonsters.size} monsters)",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        val modifier = if (dragTargetInfo.value.isDragging) {
            Modifier.background(AppColors.primaryColor)
        } else Modifier
        DropTarget<MetamobMonster>(
            Modifier.fillMaxSize().padding(2.dp).then(modifier).padding(3.dp).darkGrayBoxStyle().padding(5.dp)
        ) { _, droppedMonster ->
            if (droppedMonster != null && droppedMonster.type == MetamobMonsterType.ARCHMONSTER) {
                onListUpdate(tradeMonsters.plus(droppedMonster).distinct())
            }
            if (tradeMonsters.isEmpty()) {
                CommonText(
                    "Drag archmonsters here to add them to the trade.",
                    modifier = Modifier.padding(10.dp).align(Alignment.Center)
                )
            } else {
                SelectionContainer {
                    Box(Modifier.fillMaxSize()) {
                        val scrollState = rememberScrollState()
                        Column(Modifier.verticalScroll(scrollState).padding(end = 8.dp)) {
                            for (monster in tradeMonsters) {
                                TradeMonsterLine(monster, tradeMonsters, onListUpdate)
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(scrollState),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeMonsterLine(
    monster: MetamobMonster,
    tradeMonsters: List<MetamobMonster>,
    onListUpdate: (List<MetamobMonster>) -> Unit
) {
    Row {
        Row(Modifier.height(12.dp).align(Alignment.CenterVertically)) {
            val isHovered = remember { mutableStateOf(false) }
            ButtonWithTooltip(
                { onListUpdate(tradeMonsters.minus(monster)) },
                title = "",
                imageVector = Icons.Default.Remove,
                shape = RoundedCornerShape(percent = 5),
                width = 15.dp,
                iconColor = if (isHovered.value) Color.Black else Color.White,
                hoverBackgroundColor = AppColors.primaryColor,
                isHovered = isHovered
            )
        }
        Spacer(Modifier.width(5.dp))
        CommonText(
            monster.name,
            Modifier.align(Alignment.CenterVertically).fillMaxWidth().weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        val price = MetamobHelperUIUtil.getPrice(monster) ?: 0L
        CommonText("%,d".format(price))
    }
}

@Composable
private fun RowScope.MetamobMonstersListContent() {
    LaunchedEffect(true) {
        Thread { MetamobHelperUIUtil.refreshMonsters() }.start()
    }
    val uiState = MetamobHelperUIUtil.getUiStateValue()
    Column(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().weight(1f)) {
        Row(Modifier.height(25.dp)) {
            Row {
                RefreshButton(
                    { Thread { MetamobHelperUIUtil.refreshMonsters() }.start() },
                    "Refresh",
                    CustomShapes.buildTrapezoidShape(bottomRightDeltaRatio = 0.15f),
                    Color.Gray,
                    width = 40.dp,
                    refreshing = MetamobHelperUIUtil.refreshingMonsters
                )
            }
            CommonText(
                "Monsters",
                modifier = Modifier.align(Alignment.CenterVertically).padding(top = 5.dp, start = 10.dp),
                fontWeight = FontWeight.SemiBold,
            )
        }
        CommonText(MetamobHelperUIUtil.getLastPriceUpdateTime(), modifier = Modifier.padding(start = 10.dp, top = 5.dp))
        if (MetamobHelperUIUtil.getFilteredMonsters().isEmpty()) {
            val errorMessage = uiState.errorMessage
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
                val items = filteredMonsters.toList()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    state = state,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    items(items, key = { it.id }) { monster ->
                        Box(Modifier.height(110.dp)) {
                            MonsterCardContent(monster, items)
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