package fr.lewon.dofus.bot.gui.main.devtools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.StringUtil

@Composable
fun D2OModuleListContent() {
    val uiState = DevToolsUiUtil.getUiStateValue()
    val d2oModuleNames = D2OUtil.getModuleNames().filter {
        StringUtil.removeAccents(it).contains(StringUtil.removeAccents(uiState.nameFilter), ignoreCase = true)
    }.sorted()
    val listState = rememberLazyListState()
    Column(modifier = Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().padding(5.dp)) {
        HeaderLine()
        FilterLine(uiState)
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(end = 10.dp), state = listState) {
                items(d2oModuleNames) { d2oModuleName ->
                    D2OModuleCardContent(d2oModuleName, uiState)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(listState),
            )
        }
    }
}

@Composable
private fun HeaderLine() {
    Row(Modifier.fillMaxWidth().height(30.dp)) {
        CommonText(
            "D2O Modules",
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FilterLine(uiState: DevToolsUiState) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        CommonText(
            "Filter Name : ",
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            fontWeight = FontWeight.SemiBold
        )
        SimpleTextField(
            value = uiState.nameFilter,
            onValueChange = { DevToolsUiUtil.updateNameFilter(it) },
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun D2OModuleCardContent(d2oModuleName: String, uiState: DevToolsUiState) {
    val isHovered = remember { mutableStateOf(false) }
    val hoverAlpha = if (isHovered.value) 1f else 0.7f
    val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
    val selected = uiState.selectedD2OModule == d2oModuleName
    Row(
        modifier = Modifier.onClick { DevToolsUiUtil.selectD2OModule(d2oModuleName) }
            .border(BorderStroke(1.dp, Color.Black)).height(25.dp)
            .defaultHoverManager(isHovered)
    ) {
        Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
            animatedBackground(selected)
            Row(Modifier.fillMaxWidth().padding(3.dp)) {
                val textColor = getTextColor(selected)
                SubTitleText(
                    d2oModuleName,
                    Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                    enabledColor = textColor
                )
            }
        }
    }
}

@Composable
private fun getTextColor(selected: Boolean): Color {
    val color = if (selected) Color.Black else Color.White
    return animateColorAsState(color).value
}

@Composable
private fun animatedBackground(selected: Boolean) {
    AnimatedVisibility(
        visible = selected, enter = expandHorizontally(expandFrom = Alignment.Start), exit = fadeOut()
    ) {
        Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
    }
}