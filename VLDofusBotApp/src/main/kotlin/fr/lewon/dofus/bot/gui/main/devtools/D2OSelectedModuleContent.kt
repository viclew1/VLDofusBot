package fr.lewon.dofus.bot.gui.main.devtools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.util.StringUtil

@Composable
fun D2OSelectedModuleContent() {
    val uiState = DevToolsUiUtil.getUiStateValue()
    Column(modifier = Modifier.fillMaxSize().padding(5.dp).grayBoxStyle().padding(5.dp)) {
        HeaderLine(uiState)
        if (uiState.selectedD2OModule != null) {
            FilterLine(uiState)
            Box(Modifier.fillMaxSize()) {
                val listState = rememberLazyListState()
                val items = uiState.selectedModuleItems.filter {
                    val name = it.name
                    val id = it.id
                    (name == null || StringUtil.removeAccents(name)
                        .contains(StringUtil.removeAccents(uiState.moduleItemNameFilter))) &&
                            (id == null || uiState.moduleItemIdFilter.toIntOrNull() == 0 || StringUtil.removeAccents(id.toString())
                                .startsWith(StringUtil.removeAccents(uiState.moduleItemIdFilter)))
                }
                val expandedItems = remember(uiState.selectedD2OModule) { mutableStateOf(emptyList<ModuleItem>()) }
                LazyColumn(Modifier.fillMaxSize().padding(end = 10.dp), state = listState) {
                    items(items) { item ->
                        val expanded = expandedItems.value.contains(item)
                        SelectionContainer {
                            Column {
                                ExpandableText(
                                    text = listOfNotNull(item.id, item.name, item.content).joinToString(" - "),
                                    modifier = Modifier.fillMaxWidth().padding(2.dp),
                                    expanded = expandedItems.value.contains(item),
                                ) {
                                    val expandedItemsValue = expandedItems.value
                                    val newExpandedItems = if (expandedItemsValue.contains(item)) {
                                        expandedItemsValue.minus(item)
                                    } else {
                                        expandedItemsValue.plus(item)
                                    }
                                    expandedItems.value = newExpandedItems
                                }
                                ExpandedModuleItem(item, expanded) {
                                    expandedItems.value = expandedItems.value.minus(item)
                                }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(listState),
                )
            }
        }
    }
}

@Composable
private fun HeaderLine(uiState: DevToolsUiState) {
    Row(Modifier.fillMaxWidth().height(30.dp)) {
        CommonText(
            "Selected module : ${uiState.selectedD2OModule ?: "/"}",
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FilterLine(uiState: DevToolsUiState) {
    Column(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row {
            CommonText(
                "Filter ID : ",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            )
            IntegerTextField(
                value = uiState.moduleItemIdFilter,
                onUpdate = { DevToolsUiUtil.updateModuleItemIdFilter(if (it == "0") "" else it) },
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically).width(150.dp),
            )
        }
        Row {
            CommonText(
                "Filter Name : ",
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            )
            SimpleTextField(
                value = uiState.moduleItemNameFilter,
                onValueChange = { DevToolsUiUtil.updateModuleItemNameFilter(it) },
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically).width(150.dp),
            )
        }
    }
}

@Composable
private fun ExpandedModuleItem(moduleItem: ModuleItem, expanded: Boolean, onReduceButtonClick: () -> Unit) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
        ExpandedContent(
            title = listOfNotNull(moduleItem.id, moduleItem.name).joinToString(" - "),
            onReduceButtonClick = onReduceButtonClick,
            maxHeight = 250.dp,
            key = moduleItem
        ) {
            SelectionContainer(Modifier.padding(5.dp)) {
                Column {
                    moduleItem.id?.let { CommonText("Id : $it") }
                    moduleItem.name?.let { CommonText("Name : $it") }
                    moduleItem.description?.let { CommonText("Description : $it") }
                    HorizontalSeparator(modifier = Modifier.padding(5.dp))
                    CommonText(moduleItem.content)
                }
            }
        }
    }
}