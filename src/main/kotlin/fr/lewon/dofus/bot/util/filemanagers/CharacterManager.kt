package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.filemanagers.listeners.CharacterManagerListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CharacterManager {

    private val listeners = ArrayList<CharacterManagerListener>()

    private lateinit var characterStore: CharacterStore
    private lateinit var dataStoreFile: File

    fun initManager() {
        dataStoreFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/user_data")
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (dataStoreFile.exists()) {
            characterStore = mapper.readValue(dataStoreFile)
        } else {
            characterStore = CharacterStore()
            saveUserData()
        }
    }

    fun addListener(listener: CharacterManagerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: CharacterManagerListener) {
        listeners.remove(listener)
    }

    private fun saveUserData() {
        with(OutputStreamWriter(FileOutputStream(dataStoreFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(characterStore))
            close()
        }
    }

    fun getCharacter(pseudo: String): DofusCharacter? {
        return characterStore.characters.firstOrNull {
            it.pseudo.lowercase() == pseudo.lowercase()
        }
    }

    fun getCharacterByName(pseudo: String): DofusCharacter? {
        val matchingCharacters = characterStore.characters.filter { it.pseudo == pseudo }
        if (matchingCharacters.size != 1) {
            return null
        }
        return matchingCharacters[0]
    }

    fun getCharacters(): List<DofusCharacter> {
        return characterStore.characters.toList()
    }

    fun moveCharacter(character: DofusCharacter, toIndex: Int) {
        characterStore.characters.remove(character)
        characterStore.characters.add(toIndex, character)
        saveUserData()
        listeners.forEach { it.onCharacterMove(character, toIndex) }
    }

    fun addCharacter(pseudo: String, dofusClassId: Int, spells: List<CharacterSpell>): DofusCharacter {
        getCharacter(pseudo)?.let {
            error("Character already registered : [$pseudo]")
        }
        val character = DofusCharacter(pseudo, dofusClassId, characterSpells = ArrayList(spells))
        characterStore.characters.add(character)
        saveUserData()
        listeners.forEach { it.onCharacterCreate(character) }
        return character
    }

    fun removeCharacter(character: DofusCharacter) {
        characterStore.characters.remove(character)
        saveUserData()
        listeners.forEach { it.onCharacterDelete(character) }
    }

    fun updateCharacter(
        character: DofusCharacter,
        pseudo: String = character.pseudo,
        dofusClassId: Int = character.dofusClassId,
        spells: List<CharacterSpell> = character.characterSpells
    ) {
        val existingCharacter = getCharacter(pseudo)
        if (existingCharacter != null && existingCharacter != character) {
            error("Character already registered : [$pseudo]")
        }
        character.pseudo = pseudo
        character.dofusClassId = dofusClassId
        character.characterSpells = ArrayList(spells)
        saveUserData()
    }

    fun updateParamValue(character: DofusCharacter, script: DofusBotScript, param: DofusBotParameter) {
        character.scriptValues.updateParamValue(script.name, param.key, param.value)
        saveUserData()
    }

    fun getParamValue(character: DofusCharacter, script: DofusBotScript, param: DofusBotParameter): String? {
        return character.scriptValues.getParamValue(script.name, param.key)
    }

}
