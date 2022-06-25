package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import fr.lewon.dofus.bot.gui2.util.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> ComboBox(
    modifier: Modifier = Modifier,
    selectedItem: MutableState<T>,
    items: MutableState<List<T>>,
    onItemSelect: (item: T) -> Unit,
    getItemText: (item: T) -> String,
    maxDropDownHeight: Dp = Dp.Infinity
) {
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        OutlinedButton(
            onClick = { focusRequester.requestFocus() },
            modifier = modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onGloballyPositioned { coordinates -> textFieldSize = coordinates.size.toSize() }
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        val consumed = when (it.key) {
                            Key.Spacebar -> {
                                expanded = !expanded
                                true
                            }
                            else -> false
                        }
                        consumed
                    } else false
                }.handPointerIcon()
                .onPointerEvent(PointerEventType.Press, PointerEventPass.Initial) {
                    if (it.buttons.isPrimaryPressed) {
                        expanded = !expanded
                    }
                },
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            Row {
                Row(Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 5.dp)) {
                    Text(
                        getItemText(selectedItem.value),
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                }
                Icon(icon, "", Modifier.width(16.dp), tint = Color.White)
            }
        }
        val scrollState = rememberScrollState()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(
                with(LocalDensity.current) { textFieldSize.width.toDp() },
                Short.MAX_VALUE.toInt().dp
            )
        ) {
            val itemHeight = 20
            Box(Modifier.height(min(maxDropDownHeight, (items.value.size * itemHeight).dp))) {
                rememberCoroutineScope().launch {
                    scrollState.scrollTo(items.value.indexOf(selectedItem.value) * (itemHeight))
                }
                Column(Modifier.verticalScroll(scrollState).padding(end = 10.dp)) {
                    val popupFocusManager = LocalFocusManager.current
                    val opened = remember { mutableStateOf(false) }
                    items.value.forEach { item ->
                        val popupFocusRequester = remember { FocusRequester() }
                        Row(
                            modifier = Modifier.handPointerIcon().fillMaxWidth().padding(horizontal = 5.dp)
                                .height(itemHeight.dp)
                                .focusRequester(popupFocusRequester)
                                .onPlaced {
                                    if (!opened.value && item == selectedItem.value) {
                                        popupFocusRequester.requestFocus()
                                        opened.value = true
                                    }
                                }
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        val consumed = when (it.key) {
                                            Key.DirectionDown -> popupFocusManager.moveFocus(FocusDirection.Next)
                                            Key.DirectionUp -> popupFocusManager.moveFocus(FocusDirection.Previous)
                                            else -> false
                                        }
                                        consumed
                                    } else false
                                }
                                .clickable(
                                    onClick = {
                                        onItemSelect(item)
                                        expanded = false
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val color = if (selectedItem.value == item) AppColors.primaryColor else Color.White
                            Text(getItemText(item), fontSize = 13.sp, color = color, maxLines = 1)
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