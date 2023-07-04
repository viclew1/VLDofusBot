package fr.lewon.dofus.bot.gui.custom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.*
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.StringUtil
import kotlinx.coroutines.launch

private data class ComboBoxState<T>(
    val preSelectedItem: T,
    val selectedText: String,
    val textFieldSize: Size = Size.Zero,
    val expanded: Boolean = false,
    val filterItems: Boolean = false,
    val lastUpdateTime: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> ComboBox(
    modifier: Modifier = Modifier,
    selectedItem: T,
    items: List<T>,
    onItemSelect: (item: T) -> Unit,
    getItemText: (item: T) -> String,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    maxDropDownHeight: Dp = Dp.Infinity,
    multipleChoice: Boolean = false,
    getItemIconPainter: (item: T) -> Painter? = { null }
) {
    var state by remember(selectedItem) { mutableStateOf(ComboBoxState(selectedItem, getItemText(selectedItem))) }

    val icon = if (state.expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val filteringOptions = items.filter {
        state.expanded && (!state.filterItems || StringUtil.removeAccents(getItemText(it))
            .contains(StringUtil.removeAccents(state.selectedText), ignoreCase = true))
    }
    if (!filteringOptions.contains(state.preSelectedItem)) {
        state = state.copy(preSelectedItem = filteringOptions.firstOrNull() ?: selectedItem)
    }

    val selectNewItem = { newItem: T ->
        state = state.copy(
            preSelectedItem = newItem,
            selectedText = getItemText(newItem),
            filterItems = false,
            expanded = false,
            lastUpdateTime = System.currentTimeMillis()
        )
        onItemSelect(newItem)
    }

    fun resetState(
        preSelectedItem: T = selectedItem,
        selectedText: String = getItemText(preSelectedItem),
        filterItems: Boolean = false,
        expanded: Boolean = true,
    ) {
        state = state.copy(
            preSelectedItem = preSelectedItem,
            selectedText = selectedText,
            filterItems = filterItems,
            expanded = expanded,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    SimpleTextField(
        value = state.selectedText,
        onValueChange = {
            if (it != state.selectedText) {
                resetState(
                    preSelectedItem = selectedItem,
                    selectedText = it,
                    expanded = true,
                    filterItems = true
                )
            }
        },
        modifier = modifier.fillMaxWidth()
            .onGloballyPositioned { coordinates -> state = state.copy(textFieldSize = coordinates.size.toSize()) }
            .onTabChangeFocus(LocalFocusManager.current)
            .onFocusHighlight()
            .handPointerIcon()
            .onPointerEvent(PointerEventType.Press) {
                if (state.expanded && it.buttons.isPrimaryPressed && System.currentTimeMillis() - state.lastUpdateTime > 50) {
                    selectNewItem(state.preSelectedItem)
                }
            }
            .onFocusEvent {
                if (System.currentTimeMillis() - state.lastUpdateTime > 50) {
                    if (state.expanded && !it.isFocused && !it.hasFocus) {
                        selectNewItem(state.preSelectedItem)
                    } else if (it.isFocused && it.hasFocus && !state.expanded) {
                        resetState()
                    }
                }
            },
        backgroundColor = colors.backgroundColor(true).value,
        borderColor = colors.backgroundColor(true).value,
        trailingIcon = rememberVectorPainter(icon),
        leadingIcon = getItemIconPainter(selectedItem),
        inputHandlers = listOf(
            KeyHandler(checkKey = { it.key == Key.DirectionUp }, handleKeyEvent = {
                val index = filteringOptions.indexOf(state.preSelectedItem)
                if (index > 0) {
                    state = state.copy(preSelectedItem = filteringOptions[index - 1])
                }
            }),
            KeyHandler(checkKey = { it.key == Key.DirectionDown }, handleKeyEvent = {
                val index = filteringOptions.indexOf(state.preSelectedItem)
                if (index < filteringOptions.size - 1) {
                    state = state.copy(preSelectedItem = filteringOptions[index + 1])
                }
            }),
            KeyHandler(checkKey = { it.key == Key.Enter || it.key == Key.NumPadEnter }, handleKeyEvent = {
                if (state.expanded) {
                    selectNewItem(state.preSelectedItem)
                } else {
                    resetState()
                }
            }),
            KeyHandler(checkKey = { it.key == Key.Escape }, handleKeyEvent = {
                if (state.expanded) {
                    selectNewItem(selectedItem)
                }
            })
        )
    )

    val scrollState = rememberScrollState()
    if (filteringOptions.isNotEmpty()) {
        val itemHeight = 20
        DropdownMenu(
            expanded = state.expanded,
            onDismissRequest = { },
            focusable = false,
            modifier = Modifier.widthIn(
                with(LocalDensity.current) { state.textFieldSize.width.toDp() },
                Short.MAX_VALUE.toInt().dp
            )
        ) {
            rememberCoroutineScope().launch {
                scrollState.scrollTo(filteringOptions.indexOf(selectedItem) * (itemHeight))
            }
            Box(Modifier.height(min(maxDropDownHeight, (filteringOptions.size * itemHeight.dp)))) {
                Column(Modifier.verticalScroll(scrollState).padding(end = 10.dp)) {
                    filteringOptions.forEach { selectionOption ->
                        val color = if (selectedItem == selectionOption) {
                            AppColors.primaryColor
                        } else Color.White
                        val bgColor = if (state.preSelectedItem == selectionOption) {
                            Color.Gray
                        } else Color.Transparent
                        DropdownMenuItem(
                            onClick = { selectNewItem(selectionOption) },
                            modifier = Modifier.handPointerIcon().fillMaxWidth().padding(horizontal = 5.dp)
                                .height(itemHeight.dp).background(bgColor)
                        ) {
                            Row {
                                getItemIconPainter(selectionOption)?.let {
                                    Image(
                                        it,
                                        "",
                                        Modifier.height(25.dp).align(Alignment.CenterVertically).padding(end = 5.dp)
                                    )
                                }
                                Text(
                                    getItemText(selectionOption),
                                    fontSize = 13.sp,
                                    color = color,
                                    maxLines = 1,
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
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
