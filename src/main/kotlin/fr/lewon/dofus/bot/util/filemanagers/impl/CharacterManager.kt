package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpells
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner

object CharacterManager : FileManager<CharacterStore>("user_data", CharacterStore()) {

    private val listeners = ArrayList<CharacterManagerListener>()

    override fun getStoreClass(): Class<CharacterStore> {
        return CharacterStore::class.java
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(GlobalConfigManager)
    }

    fun addListener(listener: CharacterManagerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: CharacterManagerListener) {
        listeners.remove(listener)
    }

    fun getCharacter(pseudo: String): DofusCharacter? {
        return store.characters.firstOrNull {
            it.pseudo.lowercase() == pseudo.lowercase()
        }
    }

    fun getCharacters(): List<DofusCharacter> {
        return store.characters.toList()
    }

    fun addCharacter(pseudo: String, dofusClassId: Int, spells: List<CharacterSpell>): DofusCharacter {
        getCharacter(pseudo)?.let {
            error("Character already registered : [$pseudo]")
        }
        return DofusCharacter(pseudo, dofusClassId)
            .also { doAddCharacter(it) }
            .also { CharacterSpellManager.updateSpells(it, CharacterSpells(spells)) }
    }

    private fun doAddCharacter(character: DofusCharacter) {
        store.characters.add(character)
        saveStore()
        listeners.forEach { it.onCharacterCreate(character) }
    }

    fun removeCharacter(character: DofusCharacter, removeScriptParams: Boolean = true) {
        store.characters.remove(character)
        saveStore()
        if (removeScriptParams) {
            ScriptParamManager.removeScriptParams(character)
        }
        ScriptRunner.removeListeners(character)
        GameSnifferUtil.removeListeners(character)
        listeners.forEach { it.onCharacterDelete(character) }
    }

    fun updateCharacter(
        oldPseudo: String,
        pseudo: String,
        dofusClassId: Int,
        spells: List<CharacterSpell>
    ) {
        val storedCharacter = getCharacter(oldPseudo)
            ?: error("Character not found in store : $oldPseudo")
        val newCharacter = storedCharacter.copy(
            pseudo = pseudo,
            dofusClassId = dofusClassId,
        )
        removeCharacter(storedCharacter, oldPseudo != pseudo)
        doAddCharacter(newCharacter)
        saveStore()
        CharacterSpellManager.updateSpells(newCharacter, CharacterSpells(spells))
    }

}
