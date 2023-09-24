package fr.lewon.dofus.bot.gui.custom.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.TooltipTarget

@Composable
fun <T> CustomListContent(
    title: String,
    emptyMessage: String,
    selectedItems: List<T>,
    allItems: List<T>,
    canDeleteItem: (T) -> Boolean,
    canSelectMultipleItems: Boolean,
    onSelect: (List<T>) -> Unit,
    onDelete: (T) -> Unit = {},
    canCreateItem: Boolean = false,
    onCreate: (String) -> Unit = {},
    createItemPlaceHolder: String = "New item name",
    createItemButtonText: String = "Create item",
    itemCardMainContent: @Composable RowScope.(item: T, textColor: Color) -> Unit
) {
    val allSelected = remember { mutableStateOf(false) }
    val nonAvailableSelectedItems = selectedItems.filter { it !in allItems }
    onSelect(selectedItems.minus(nonAvailableSelectedItems.toSet()))
    if (!canSelectMultipleItems && selectedItems.size > 1) {
        val toSelectItem = allItems.firstOrNull { it in selectedItems }
        val toUnselect = allItems.toMutableList()
        if (toSelectItem != null) {
            toUnselect.remove(toSelectItem)
            onSelect(listOf(toSelectItem))
        } else {
            onSelect(emptyList())
        }
    }
    if (selectedItems.isEmpty()) {
        allSelected.value = false
    } else if (selectedItems.size == allItems.size) {
        allSelected.value = true
    }
    CustomStyledColumn(headerContent = {
        Row(Modifier.height(30.dp)) {
            CommonText(
                title,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.fillMaxWidth().weight(1f))
            if (canSelectMultipleItems) {
                TooltipTarget(if (allSelected.value) "Unselect all" else "Select all") {
                    Checkbox(allSelected.value, { newValue ->
                        allSelected.value = newValue
                        if (newValue) {
                            onSelect(allItems)
                        } else {
                            onSelect(emptyList())
                        }
                    }, modifier = Modifier.handPointerIcon())
                }
            }
        }
    }, Modifier.padding(5.dp).grayBoxStyle()) {
        if (canCreateItem) {
            ItemCreationLine(allItems, createItemPlaceHolder, createItemButtonText, onCreate)
        }
        if (allItems.isEmpty()) {
            Box(Modifier.padding(horizontal = 5.dp, vertical = 20.dp)) {
                CommonText(
                    emptyMessage,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            CustomVerticalScrollable(columnModifier = Modifier.fillMaxHeight()) {
                for (item in allItems) {
                    Column(Modifier.height(30.dp)) {
                        CustomListItem(
                            item,
                            selectedItems = selectedItems,
                            canSelectMultipleItems = canSelectMultipleItems,
                            onSelect = onSelect,
                            onDelete = onDelete,
                            canDeleteItem = canDeleteItem,
                            itemCardMainContent = itemCardMainContent
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun <T> ItemCreationLine(
    items: List<T>,
    createItemPlaceHolder: String,
    createItemButtonText: String,
    onCreate: (String) -> Unit
) {
    val newItemName = remember(items.size) { mutableStateOf("") }
    Row(Modifier.fillMaxWidth().padding(bottom = 2.dp).padding(end = 5.dp)) {
        Column(Modifier.fillMaxWidth().weight(1f)) {
            SimpleTextField(
                newItemName.value,
                onValueChange = { newItemName.value = it },
                modifier = Modifier.padding(5.dp).fillMaxWidth(),
                placeHolderText = createItemPlaceHolder
            )
        }
        val enabled = newItemName.value.isNotBlank()
        Row(Modifier.height(30.dp).align(Alignment.CenterVertically)) {
            ButtonWithTooltip(
                onClick = {
                    onCreate(newItemName.value.trim())
                },
                title = createItemButtonText,
                imageVector = Icons.Default.Add,
                shape = RoundedCornerShape(percent = 10),
                enabled = enabled,
                iconColor = if (enabled) Color.White else Color.Black,
            )
        }
    }
}