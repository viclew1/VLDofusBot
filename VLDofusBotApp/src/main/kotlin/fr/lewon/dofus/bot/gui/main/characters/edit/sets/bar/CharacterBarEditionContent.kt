package fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.DragTarget
import fr.lewon.dofus.bot.gui.main.DropTarget
import fr.lewon.dofus.bot.gui.main.TooltipPlacement
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.sets.CharacterSetElement
import fr.lewon.dofus.bot.util.StringUtil
import kotlinx.coroutines.launch

private val keys = listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')

@Composable
fun <T> CharacterElementBarEditionContent(
    characterUIState: CharacterUIState,
    setElements: List<CharacterSetElement>,
    itemType: ElementItemType,
    availableElements: List<T>,
    getElementById: (Int) -> T?,
    getElementName: (T) -> String,
    getElementId: (T) -> Int,
    getElementImageContent: @Composable (T) -> Unit,
    getElementTooltipContent: @Composable RowScope.(T) -> Unit,
    updateElementId: (key: Char, ctrlModifier: Boolean, id: Int?) -> Unit,
) {
    val nameFilter = remember(characterUIState.name) { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.padding(5.dp)) {
        Row(Modifier.fillMaxWidth()) {
            SimpleTextField(
                text = nameFilter.value,
                onValueChange = {
                    coroutineScope.launch {
                        nameFilter.value = it
                    }
                },
                modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
                placeHolderText = "Search ${itemType.name.lowercase()}"
            )
        }
        Row(Modifier.fillMaxSize().weight(1f)) {
            AvailableElements(
                itemType,
                nameFilter.value,
                availableElements,
                getElementName,
                getElementId,
                getElementImageContent,
                getElementTooltipContent,
                updateElementId
            )
        }
        Spacer(Modifier.height(15.dp))
        CharacterElementBar(
            setElements,
            itemType,
            getElementById,
            getElementId,
            getElementImageContent,
            getElementTooltipContent,
            updateElementId,
            draggable = true
        )
    }
}

@Composable
fun <T> CharacterElementBar(
    setElements: List<CharacterSetElement>,
    itemType: ElementItemType,
    getElementById: (Int) -> T?,
    getElementId: (T) -> Int,
    getElementImageContent: @Composable (T) -> Unit,
    getElementTooltipContent: @Composable RowScope.(T) -> Unit,
    updateElementId: (key: Char, ctrlModifier: Boolean, id: Int?) -> Unit,
    draggable: Boolean = false,
    includeTitle: Boolean = true,
) {
    Column {
        if (includeTitle) {
            CommonText(
                "${itemType.name.lowercase().capitalize(Locale.current)} bar",
                Modifier.padding(3.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(Modifier.fillMaxWidth().darkGrayBoxStyle().padding(4.dp)) {
            ElementsRow(
                getElementLine(false, setElements),
                itemType,
                getElementById,
                getElementId,
                getElementImageContent,
                getElementTooltipContent,
                updateElementId,
                draggable
            )
            ElementsRow(
                getElementLine(true, setElements),
                itemType,
                getElementById,
                getElementId,
                getElementImageContent,
                getElementTooltipContent,
                updateElementId,
                draggable
            )
        }
    }
}

private fun getElementLine(
    ctrlModifier: Boolean,
    setElements: List<CharacterSetElement>,
): List<CharacterSetElement> {
    return keys.map { key ->
        setElements.firstOrNull { it.key == key && it.ctrlModifier == ctrlModifier }
            ?: CharacterSetElement(null, key, ctrlModifier)
    }
}

@Composable
private fun <T> ElementsRow(
    characterSetElements: List<CharacterSetElement>,
    itemType: ElementItemType,
    getElementById: (Int) -> T?,
    getElementId: (T) -> Int,
    getElementImageContent: @Composable (T) -> Unit,
    getElementTooltipContent: @Composable RowScope.(T) -> Unit,
    updateElementId: (key: Char, ctrlModifier: Boolean, id: Int?) -> Unit,
    draggable: Boolean,
) {
    Row {
        for (characterSetElement in characterSetElements) {
            Row(Modifier.fillMaxWidth().weight(1f).aspectRatio(1f).padding(1.dp)) {
                val element = characterSetElement.elementId?.let(getElementById)
                if (element != null) {
                    TooltipTarget(
                        key = getElementId(element),
                        tooltipContent = { getElementTooltipContent(element) },
                        tooltipPlacement = TooltipPlacement.TopCornerAttached,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        ElementBox {
                            ElementContent(
                                characterSetElement,
                                itemType,
                                getElementById,
                                getElementId,
                                getElementImageContent,
                                updateElementId,
                                draggable
                            )
                        }
                    }
                } else {
                    ElementBox {
                        ElementContent(
                            characterSetElement,
                            itemType,
                            getElementById,
                            getElementId,
                            getElementImageContent,
                            updateElementId,
                            draggable
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ElementBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isHovered = remember { mutableStateOf(false) }
    val bgColor = if (isHovered.value) Color.Gray else AppColors.backgroundColor
    Box(
        modifier.fillMaxSize().background(bgColor).defaultHoverManager(isHovered)
            .border(BorderStroke(1.dp, AppColors.VERY_DARK_BG_COLOR)).padding(1.dp)
    ) {
        content()
    }
}

@Composable
private fun <T> ElementContent(
    setElement: CharacterSetElement,
    itemType: ElementItemType,
    getElementById: (Int) -> T?,
    getElementId: (T) -> Int,
    getElementImageContent: @Composable (T) -> Unit,
    updateElementId: (key: Char, ctrlModifier: Boolean, id: Int?) -> Unit,
    draggable: Boolean,
) {
    val key = setElement.key
    val ctrlModifier = setElement.ctrlModifier
    val element = setElement.elementId?.let(getElementById)
    val elementKey = ElementKey(key, ctrlModifier)
    val bgColor = remember { mutableStateOf(Color.Transparent) }
    Box(Modifier.fillMaxSize().background(bgColor.value)) {
        element?.let {
            ElementBox(itemType, element, elementKey, draggable) {
                getElementImageContent(element)
            }
        }
        DropTarget<ElementDrag<T>>(Modifier.fillMaxSize()) { isInBound, elementDrag ->
            bgColor.value = if (isInBound) {
                AppColors.primaryColor
            } else Color.Transparent
            if (elementDrag?.itemType == itemType) {
                if (isInBound && elementDrag.fromKey != elementKey) {
                    val fromKey = elementDrag.fromKey
                    if (fromKey != null && elementKey != fromKey) {
                        updateElementId(fromKey.key, fromKey.ctrlModifier, null)
                    }
                    updateElementId(key, ctrlModifier, getElementId(elementDrag.element))
                }
            }
        }
    }
}

@Composable
private fun <T> ElementBox(
    itemType: ElementItemType,
    element: T,
    key: ElementKey?,
    draggable: Boolean,
    imageContent: @Composable () -> Unit,
) {
    val content = @Composable {
        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.2f))) {
            imageContent()
        }
    }
    if (draggable) {
        DragTarget(ElementDrag(itemType, element, key)) {
            content()
        }
    } else content()
}

@Composable
private fun <T> AvailableElements(
    itemType: ElementItemType,
    nameFilter: String,
    availableElements: List<T>,
    getElementName: (T) -> String,
    getElementId: (T) -> Int,
    getElementImageContent: @Composable (T) -> Unit,
    getElementTooltipContent: @Composable RowScope.(T) -> Unit,
    updateElementId: (key: Char, ctrlModifier: Boolean, id: Int?) -> Unit,
) {
    val filteredElements = availableElements.filter {
        StringUtil.removeAccents(getElementName(it)).contains(StringUtil.removeAccents(nameFilter), ignoreCase = true)
    }
    DropTarget<ElementDrag<T>>(Modifier.fillMaxSize()) { isInBound, elementDrag ->
        if (isInBound && elementDrag?.fromKey != null) {
            updateElementId(elementDrag.fromKey.key, elementDrag.fromKey.ctrlModifier, null)
        }
        Box(Modifier.fillMaxSize().darkGrayBoxStyle().padding(5.dp)) {
            val state = rememberScrollState()
            VerticalGrid(
                columns = 10,
                modifier = Modifier.padding(end = 8.dp).verticalScroll(state),
                items = filteredElements
            ) { element ->
                ElementBox(Modifier.padding(1.dp)) {
                    TooltipTarget(
                        key = getElementId(element),
                        tooltipContent = { getElementTooltipContent(element) },
                        tooltipPlacement = TooltipPlacement.TopCornerAttached,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        ElementBox(itemType, element, null, true) {
                            getElementImageContent(element)
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(state),
            )
        }
    }
}