package fr.lewon.dofus.bot.gui.custom.list

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.ButtonWithTooltip
import fr.lewon.dofus.bot.gui.custom.CustomShapes
import fr.lewon.dofus.bot.gui.custom.defaultHoverManager
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.util.AppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CustomListItem(
    item: T,
    selectedItems: List<T>,
    canSelectMultipleItems: Boolean,
    onSelect: (List<T>) -> Unit,
    onDelete: (T) -> Unit,
    canDeleteItem: (T) -> Boolean,
    itemCardMainContent: @Composable RowScope.(item: T, textColor: Color) -> Unit
) {
    val selected = item in selectedItems
    val isHovered = remember { mutableStateOf(false) }
    val hoverAlpha = if (isHovered.value) 1f else 0.7f
    val backgroundColor = Color.DarkGray.copy(alpha = hoverAlpha)
    Row(modifier = Modifier.border(BorderStroke(1.dp, Color.Black)).defaultHoverManager(isHovered)) {
        Box(Modifier.fillMaxSize().weight(1f).background(backgroundColor)) {
            AnimatedBackgroundSelectedColor(selected)
            Row(Modifier.fillMaxSize().onClick {
                onSelect(listOf(item))
            }.onClick(keyboardModifiers = { canSelectMultipleItems && isCtrlPressed }) {
                if (selected) {
                    onSelect(selectedItems.minus(item))
                } else {
                    onSelect(selectedItems.plus(item))
                }
            }) {
                itemCardMainContent(item, getTextColor(selected))
            }
            HoverButtons(item, isHovered, onDelete, canDeleteItem)
        }
        AnimatedVisibility(
            visible = canSelectMultipleItems,
            enter = expandHorizontally(expandFrom = Alignment.End),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End)
        ) {
            SelectedCheckbox(item, selected, selectedItems, onSelect)
        }
    }
}

@Composable
private fun getTextColor(selected: Boolean): Color {
    val color = if (selected) Color.Black else Color.White
    return animateColorAsState(color).value
}

@Composable
private fun AnimatedBackgroundSelectedColor(selected: Boolean) {
    AnimatedVisibility(
        visible = selected, enter = expandHorizontally(expandFrom = Alignment.Start), exit = fadeOut()
    ) {
        Row(Modifier.fillMaxSize().background(AppColors.primaryLightColor)) {}
    }
}

@Composable
private fun <T> HoverButtons(
    item: T,
    isHovered: MutableState<Boolean>,
    onDelete: (T) -> Unit,
    canDeleteItem: (T) -> Boolean,
) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.align(Alignment.TopEnd).fillMaxHeight(0.6f)) {
            if (isHovered.value && canDeleteItem(item)) {
                DeleteButton(item, onDelete)
            }
        }
    }
}

@Composable
private fun <T> DeleteButton(item: T, onDelete: (T) -> Unit) {
    ButtonWithTooltip(
        { onDelete(item) },
        "Delete",
        Icons.Default.Close,
        CustomShapes.buildTrapezoidShape(bottomLeftDeltaRatio = 0.15f),
        AppColors.RED
    )
}

@Composable
private fun <T> SelectedCheckbox(item: T, selected: Boolean, selectedItems: List<T>, onSelect: (List<T>) -> Unit) {
    val checkBoxBackgroundColor = Color.Gray.copy(alpha = 0.5f)
    Row(Modifier.handPointerIcon().background(checkBoxBackgroundColor).width(30.dp)) {
        Checkbox(
            selected,
            {
                if (it) {
                    onSelect(selectedItems.plus(item))
                } else {
                    onSelect(selectedItems.minus(item))
                }
            },
            Modifier.fillMaxWidth(),
            colors = CheckboxDefaults.colors(checkmarkColor = Color.Black, disabledColor = Color.Black),
        )
    }
}