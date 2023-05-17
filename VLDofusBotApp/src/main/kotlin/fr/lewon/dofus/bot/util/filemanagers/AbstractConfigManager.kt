package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

abstract class AbstractConfigManager<T>(private val fileName: String) : ToInitManager {

    private var config: T? = null
    private lateinit var configFile: File

    override fun initManager() {
        configFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/$fileName")
        if (configFile.exists()) {
            config = deserializeConfig(configFile)
        } else {
            config = createNewConfig()
            saveConfig()
        }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
    }

    protected abstract fun deserializeConfig(file: File): T

    protected abstract fun createNewConfig(): T

    protected abstract fun copyConfig(config: T): T

    fun readConfig(): T {
        return config?.let { copyConfig(it) } ?: error("Config hasn't been properly initialized")
    }

    fun editConfig(function: (T) -> Unit) {
        val config = this.config ?: error("Config hasn't been properly initialized")
        function(config)
        saveConfig()
    }

    private fun saveConfig() {
        with(OutputStreamWriter(FileOutputStream(configFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(config))
            close()
        }
    }

}