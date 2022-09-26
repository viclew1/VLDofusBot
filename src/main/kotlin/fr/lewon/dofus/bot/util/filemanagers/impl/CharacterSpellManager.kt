package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells
import fr.lewon.dofus.bot.model.characters.spells.SpellStore
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object CharacterSpellManager : FileManager<SpellStore>("spells", SpellStore()) {

    override fun getStoreClass(): Class<SpellStore> {
        return SpellStore::class.java
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(CharacterManager)
    }

    fun getSpells(character: DofusCharacter): CharacterSpells {
        return store.getCharacterSpells(character).deepCopy()
    }

    fun updateSpells(character: DofusCharacter, spells: CharacterSpells) {
        store.setCharacterSpells(character, spells.deepCopy())
        saveStore()
    }

}