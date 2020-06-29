package fr.lewon.dofus.bot.util

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.json.DTBConfig
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


object DTBConfigManager {

    val config: DTBConfig
    private val configFile: File = File("config/config")
    private val mapper = ObjectMapper()

    init {
        val module = SimpleModule()
        module.addKeyDeserializer(Directions::class.java, DirectionKeyDeserializer())
        mapper.registerModule(module)
        if (configFile.exists()) {
            config = ObjectMapper().readValue(configFile)
        } else {
            config = DTBConfig()
            saveConfig()
        }
    }

    private class DirectionKeyDeserializer : KeyDeserializer() {
        override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any {
            key ?: error("Cannot deserialize [$key]")
            return Directions.valueOf(key)
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