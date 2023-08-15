package fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.lewon.dofus.bot.core.d2o.managers.item.ItemManager
import fr.lewon.dofus.bot.core.d2p.gfx.D2PItemsGfxAdapter
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.CharacterSetsUiUtil
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.CharacterElementBar
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.CharacterElementBarEditionContent
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.ElementItemType
import fr.lewon.dofus.bot.gui.util.toPainter

@Composable
fun CharacterItemsEditionContent(characterUIState: CharacterUIState) {
    CharacterElementBarEditionContent(
        characterUIState = characterUIState,
        setElements = CharacterSetsUiUtil.getCurrentSet(characterUIState.name).items,
        itemType = ElementItemType.ITEM,
        availableElements = CharacterSetsUiUtil.availableItems,
        getElementById = { ItemManager.getItem(it.toDouble()) },
        getElementName = { it.name },
        getElementImageContent = { ItemImageContent(it) },
        getElementId = { it.id.toInt() },
        getElementTooltipContent = { CommonText(it.name) },
        updateElementId = { key, ctrlModifier, itemId ->
            CharacterSetsUiUtil.updateItemId(characterUIState.name, key, ctrlModifier, itemId)
        }
    )
}

@Composable
fun CharacterItemBar(characterName: String, includeTitle: Boolean = true) = CharacterElementBar(
    itemType = ElementItemType.ITEM,
    setElements = CharacterSetsUiUtil.getCurrentSet(characterName).items,
    getElementById = { ItemManager.getItem(it.toDouble()) },
    getElementId = { it.id.toInt() },
    getElementTooltipContent = { CommonText(it.name) },
    getElementImageContent = { ItemImageContent(it) },
    updateElementId = { key, ctrlModifier, itemId ->
        CharacterSetsUiUtil.updateItemId(characterName, key, ctrlModifier, itemId)
    },
    includeTitle = includeTitle
)

@Composable
private fun ItemImageContent(item: DofusItem) {
    val gfxImageData = D2PItemsGfxAdapter.getItemIconData(item.iconId.toDouble())
    Image(gfxImageData.toPainter(), "", Modifier.fillMaxSize())
}