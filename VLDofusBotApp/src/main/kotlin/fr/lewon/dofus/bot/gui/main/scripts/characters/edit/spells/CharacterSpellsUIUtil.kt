package fr.lewon.dofus.bot.gui.main.scripts.characters.edit.spells

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.CharacterEditionUIUtil
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSpellManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterSpellManagerListener

object CharacterSpellsUIUtil : ComposeUIUtil(), CharacterSpellManagerListener {

    private val uiState = mutableStateOf(CharacterSpellsUIState())

    fun getCharacterSpell(key: Char, ctrlModifier: Boolean): CharacterSpell? {
        return uiState.value.spellByKey[SpellKey(key, ctrlModifier)]
    }

    override fun onSpellsUpdate(characterName: String, spells: CharacterSpells) {
        if (characterName == getEditedCharacterName()) {
            updateSpells(spells)
        }
    }

    fun updateSpells(spells: CharacterSpells) {
        uiState.value = uiState.value.copy(
            spellByKey = spells.associateBy { SpellKey(it.key, it.ctrlModifier) }
        )
    }

    fun updateSpellId(key: Char, ctrlModifier: Boolean, spellId: Int?) {
        val editedCharacterName = getEditedCharacterName() ?: error("No edited character")
        CharacterSpellManager.updateSpell(editedCharacterName, key, ctrlModifier, spellId)
    }

    private fun getEditedCharacterName(): String? {
        return CharacterEditionUIUtil.getEditedCharacterUIState()?.value?.name
    }

}