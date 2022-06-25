package fr.lewon.dofus.bot.gui2.init

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.AnimatedButton
import fr.lewon.dofus.bot.gui2.custom.CustomShapes
import fr.lewon.dofus.bot.gui2.util.UiResource
import fr.lewon.dofus.bot.gui2.util.getScaledImage

@Composable
fun InitBottomBar() {
    if (InitUIState.INIT_SUCCESS.value) {
        successBottomBar()
    } else if (InitUIState.ERRORS_ON_INIT.value) {
        errorBottomBar()
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
    Column {
        retryButton(Modifier.size(80.dp, 30.dp).align(Alignment.End))
        BottomAppBar(modifier = Modifier.height(210.dp), cutoutShape = CircleShape) {
            Column {
                Box(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                    val state = rememberScrollState()
                    SelectionContainer {
                        Text(
                            text = "Initialization KO : ${InitUIState.ERRORS.value.joinToString("") { "\n - $it" }}",
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
private fun retryButton(modifier: Modifier) {
    AnimatedButton(
        { InitUIState.initAll() },
        "Retry",
        UiResource.RETRY.imageData.getScaledImage(32).toPainter(),
        modifier,
        CustomShapes.buildTrapezoidShape(topLeftDeltaRatio = 0.15f)
    )
}
