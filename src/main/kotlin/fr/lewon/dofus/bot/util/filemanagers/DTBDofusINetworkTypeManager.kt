package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

abstract class DTBDofusINetworkTypeManager(filePath: String) {

    private val idByName: Map<String, Int>
    private val nameById: Map<Int, String>
    private val protocolFile: File = File(filePath)

    init {
        if (protocolFile.exists()) {
            idByName = ObjectMapper().readValue(protocolFile)
            nameById = idByName.entries.associateBy({ it.value }) { it.key }
        } else {
            error("Missing file : $filePath.")
        }
    }

    fun getName(id: Int): String? {
        return nameById[id]
    }

    fun getId(name: String): Int? {
        return idByName[name]
    }
}