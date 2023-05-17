package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.model.characters.scriptvalues.CharacterScriptValues
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValuesStore
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import java.util.concurrent.locks.ReentrantLock

object ScriptValuesManager : ToInitManager {

    private val lock = ReentrantLock()
    private lateinit var fileManager: FileManager<ScriptValuesStore>

    override fun initManager() {
        fileManager = FileManager("script_values", ScriptValuesStore())
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(CharacterManager)
    }

    fun getCharacterScriptValues(characterName: String): CharacterScriptValues {
        return lock.executeSyncOperation {
            fileManager.getElement { store ->
                store.getScriptValues(characterName).deepCopy()
            }
        }
    }

    fun updateParamValue(
        characterName: String,
        scriptBuilder: DofusBotScriptBuilder,
        parameter: DofusBotParameter,
        value: String
    ) {
        lock.executeSyncOperation {
            fileManager.updateStore { store ->
                store.getScriptValues(characterName).getValues(scriptBuilder).updateParamValue(parameter, value)
            }
        }
    }

    fun removeScriptValues(characterName: String) {
        lock.executeSyncOperation {
            fileManager.updateStore { store ->
                store.remove(characterName)
            }
        }
    }

}