package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.model.characters.CharacterStore
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.Listenable
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CharacterManager : Listenable<CharacterManagerListener>(), ToInitManager {

    private lateinit var characterStore: CharacterStore
    private lateinit var dataStoreFile: File

    override fun initManager() {
        dataStoreFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/user_data")
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (dataStoreFile.exists()) {
            characterStore = mapper.readValue(dataStoreFile)
        } else {
            characterStore = CharacterStore()
            saveCharacterStore()
        }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(GlobalConfigManager)
    }

    private fun saveCharacterStore() {
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
        saveCharacterStore()
        listeners.forEach { it.onCharacterMove(character, toIndex) }
    }

    fun addCharacter(pseudo: String, dofusClassId: Int, spells: List<CharacterSpell>): DofusCharacter {
        getCharacter(pseudo)?.let {
            error("Character already registered : [$pseudo]")
        }
        val character = DofusCharacter(pseudo, dofusClassId, characterSpells = ArrayList(spells))
        characterStore.characters.add(character)
        saveCharacterStore()
        listeners.forEach { it.onCharacterCreate(character) }
        return character
    }

    fun removeCharacter(character: DofusCharacter) {
        characterStore.characters.remove(character)
        saveCharacterStore()
        ScriptRunner.removeListeners(character)
        GameSnifferUtil.removeListeners(character)
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
        saveCharacterStore()
    }

    fun updateParamValue(
        character: DofusCharacter,
        script: DofusBotScriptBuilder,
        param: DofusBotParameter,
        value: String
    ) {
        character.scriptValuesStore.getValues(script).updateParamValue(param, value)
        saveCharacterStore()
    }

    fun getParamValue(character: DofusCharacter, script: DofusBotScriptBuilder, param: DofusBotParameter): String {
        return character.scriptValuesStore.getValues(script.name).getParamValue(param)
    }

}
