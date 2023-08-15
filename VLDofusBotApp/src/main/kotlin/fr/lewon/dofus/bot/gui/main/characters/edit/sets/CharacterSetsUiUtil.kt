package fr.lewon.dofus.bot.gui.main.characters.edit.sets

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.item.ItemManager
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.model.characters.sets.CharacterSetElement
import fr.lewon.dofus.bot.model.characters.sets.CharacterSets
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSetsManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterSetManagerListener
import fr.lewon.dofus.bot.util.ids.ItemIds
import java.util.concurrent.locks.ReentrantLock

object CharacterSetsUiUtil : ComposeUIUtil(), CharacterSetManagerListener {

    private val lock = ReentrantLock()
    private val uiState = mutableStateOf(CharacterSetsUiState())

    lateinit var availableItems: List<DofusItem>

    override fun init() {
        val itemIds = ItemIds.EMPTY_SOUL_STONE_ITEM_IDS
            .plus(ItemIds.RECALL_POTION_ITEM_ID)
            .plus(ItemIds.TREASURE_HUNT_CHEST_ITEM_IDS)
            .sorted()
        availableItems = itemIds.map { ItemManager.getItem(it.toDouble()) }
    }

    fun getUiStateValue() = lock.executeSyncOperation {
        uiState.value
    }

    override fun onSetsUpdate(characterName: String, sets: CharacterSets) = lock.executeSyncOperation {
        uiState.value = uiState.value.copy(
            setsByCharacterName = uiState.value.setsByCharacterName.plus(characterName to sets.copy())
        )
    }

    fun getCurrentSet(characterName: String): CharacterSet = lock.executeSyncOperation {
        getUiStateValue().setsByCharacterName[characterName]?.getSelectedSet() ?: CharacterSet()
    }

    fun updateSpellId(
        characterName: String,
        key: Char,
        ctrlModifier: Boolean,
        spellId: Int?,
    ) = lock.executeSyncOperation {
        val set = getCurrentSet(characterName)
        CharacterSetsManager.updateSet(
            characterName,
            set.copy(spells = getNewSetElements(set.spells, key, ctrlModifier, spellId))
        )
    }

    fun updateItemId(
        characterName: String,
        key: Char,
        ctrlModifier: Boolean,
        itemId: Int?,
    ) = lock.executeSyncOperation {
        val set = getCurrentSet(characterName)
        CharacterSetsManager.updateSet(
            characterName,
            set.copy(items = getNewSetElements(set.items, key, ctrlModifier, itemId))
        )
    }

    private fun getNewSetElements(
        elements: List<CharacterSetElement>,
        key: Char,
        ctrlModifier: Boolean,
        elementId: Int?,
    ): List<CharacterSetElement> {
        val newElements = elements.toMutableList()
        val newElement = CharacterSetElement(elementId, key, ctrlModifier)
        val index = newElements.indexOfFirst { it.key == key && it.ctrlModifier == ctrlModifier }
        if (index >= 0) {
            newElements[index] = newElement
        } else {
            newElements.add(newElement)
        }
        return newElements
    }

}