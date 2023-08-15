package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomStyledColumn(
    headerTitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CustomStyledColumn(headerContent = {
        Row(Modifier.height(30.dp)) {
            CommonText(
                headerTitle,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
    }, modifier = modifier, content = content)

}

@Composable
fun CustomStyledColumn(
    headerContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.grayBoxStyle()) {
        Box(Modifier.fillMaxWidth().darkGrayBoxStyle()) {
            headerContent()
        }
        content()
    }
}