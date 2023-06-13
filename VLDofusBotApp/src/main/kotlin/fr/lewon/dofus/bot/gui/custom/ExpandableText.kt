package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import fr.lewon.dofus.bot.gui.util.AppColors


@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandButtonClick: () -> Unit,
) {
    Column(modifier) {
        Row {
            val color = if (expanded) AppColors.primaryColor else Color.White
            Row(Modifier.height(12.dp).align(Alignment.CenterVertically)) {
                val icon = if (expanded) Icons.Default.Remove else Icons.Default.Add
                val isHovered = remember { mutableStateOf(false) }
                ButtonWithTooltip(
                    onClick = onExpandButtonClick,
                    title = if (expanded) "Reduce" else "Expand",
                    imageVector = icon,
                    shape = RoundedCornerShape(percent = 5),
                    width = 20.dp,
                    iconColor = if (isHovered.value) Color.Black else Color.White,
                    hoverBackgroundColor = AppColors.primaryColor,
                    isHovered = isHovered
                )
            }
            Spacer(Modifier.width(5.dp))
            Row(Modifier.align(Alignment.CenterVertically)) {
                SelectionContainer {
                    CommonText(text, enabledColor = color, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun ExpandedContent(
    title: String,
    onReduceButtonClick: () -> Unit,
    key: Any,
    defaultHeight: Dp = 40.dp,
    maxHeight: Dp = 130.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val descriptionScrollState = remember(key) { ScrollState(0) }
    val height = remember { mutableStateOf(defaultHeight) }
    Column(
        Modifier.fillMaxWidth().height(min(height.value, maxHeight)).background(AppColors.backgroundColor).border(
            BorderStroke(1.dp, Color.Gray)
        ).then(modifier)
    ) {
        Row(Modifier.height(25.dp)) {
            ButtonWithTooltip(
                onClick = onReduceButtonClick,
                title = "Reduce",
                imageVector = Icons.Default.Remove,
                shape = CustomShapes.buildTrapezoidShape(),
                width = 30.dp,
                defaultBackgroundColor = AppColors.primaryColor,
                hoverBackgroundColor = AppColors.primaryColor,
                iconColor = Color.Black
            )
            Spacer(Modifier.width(5.dp))
            Row(Modifier.align(Alignment.CenterVertically)) {
                SelectionContainer {
                    CommonText(title, fontWeight = FontWeight.Bold)
                }
            }
        }
        HorizontalSeparator()
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.onGloballyPositioned {
                height.value = it.size.height.dp + defaultHeight
            }.padding(end = 14.dp)) {
                SelectionContainer(Modifier.verticalScroll(descriptionScrollState)) {
                    content()
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(descriptionScrollState),
            )
        }
    }
}