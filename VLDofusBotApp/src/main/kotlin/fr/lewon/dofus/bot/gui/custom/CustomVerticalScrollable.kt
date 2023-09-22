package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun CustomVerticalScrollable(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier) {
        val scrollState = rememberScrollState()
        Column(columnModifier.padding(end = 10.dp).verticalScroll(scrollState)) {
            content()
        }
        Row(Modifier.width(10.dp).align(Alignment.CenterEnd)) {
            VerticalScrollbar(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    .background(AppColors.VERY_DARK_BG_COLOR)
                    .padding(start = 2.dp, end = 4.dp).padding(vertical = 3.dp),
                adapter = rememberScrollbarAdapter(scrollState),
            )
        }
    }
}