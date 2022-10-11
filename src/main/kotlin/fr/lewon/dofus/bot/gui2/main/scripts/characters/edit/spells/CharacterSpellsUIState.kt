package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.spells

import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell

data class CharacterSpellsUIState(
    val spellIdByKey: Map<SpellKey, CharacterSpell?> = HashMap(),
)