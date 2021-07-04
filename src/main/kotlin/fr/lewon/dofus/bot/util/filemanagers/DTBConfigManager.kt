package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.DTBConfig
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


object DTBConfigManager {

    val config: DTBConfig
    private val configFile: File = File("config/config")

    init {
        if (configFile.exists()) {
            config = ObjectMapper().readValue(configFile)
        } else {
            config = DTBConfig()
            saveConfig()
        }
    }

    fun editConfig(function: (DTBConfig) -> Unit) {
        function.invoke(config)
        saveConfig()
    }

    private fun saveConfig() {
        with(OutputStreamWriter(FileOutputStream(configFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(config))
            close()
        }
    }
}