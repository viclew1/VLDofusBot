package fr.lewon.dofus.bot.gui.init

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CustomShapes
import fr.lewon.dofus.bot.gui.custom.RefreshButton
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun InitBottomBar() {
    val initUIState = InitUIUtil.initUiState.value
    if (initUIState.error.isNotBlank()) {
        errorBottomBar()
    } else if (!initUIState.executing) {
        successBottomBar()
    }
}

@Composable
private fun successBottomBar() {
    BottomAppBar {
        Text(
            "Initialization OK !",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Green,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun errorBottomBar() {
    val initUIState = InitUIUtil.initUiState.value
    Column {
        retryButton()
        BottomAppBar(modifier = Modifier.height(210.dp), cutoutShape = CircleShape) {
            Column {
                Box(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                    val state = rememberScrollState()
                    SelectionContainer {
                        Text(
                            text = "Initialization KO : ${initUIState.error}",
                            color = Color.Red,
                            modifier = Modifier.fillMaxSize().padding(end = 10.dp).verticalScroll(state)
                        )
                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(state),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.retryButton() {
    val isHovered = remember { mutableStateOf(false) }
    Row(Modifier.size(40.dp, 30.dp).align(Alignment.End)) {
        RefreshButton(
            { InitUIUtil.initAll() },
            "",
            shape = CustomShapes.buildTrapezoidShape(topLeftDeltaRatio = 0.15f),
            width = 40.dp,
            imageModifier = Modifier.fillMaxWidth(),
            isHovered = isHovered,
            iconColor = if (isHovered.value) Color.Black else Color.White,
            hoverBackgroundColor = AppColors.primaryColor
        )
    }
}
