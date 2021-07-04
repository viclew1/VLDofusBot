package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object DTBSwfScriptManager {

    private val uuidByMessageName: Map<String, Int>
    private val scriptNameByUuid: Map<Int, String>
    private val protocolFile: File = File("config/uuids")

    init {
        if (protocolFile.exists()) {
            uuidByMessageName = ObjectMapper().readValue(protocolFile)
            scriptNameByUuid = uuidByMessageName.entries.associateBy({ it.value }) { it.key }
        } else {
            error("Missing uuid file.")
        }
    }

    fun getScriptName(uuid: Int): String {
        return scriptNameByUuid[uuid] ?: error("Couldn't find script name for uuid [$uuid]")
    }

    fun getUuid(scriptName: String): Int {
        return uuidByMessageName[scriptName] ?: error("Couldn't find uuid for message [$scriptName]")
    }

}