package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.GlobalConfig
import fr.lewon.dofus.bot.util.filemanagers.AbstractConfigManager
import java.io.File


object GlobalConfigManager : AbstractConfigManager<GlobalConfig>("config") {

    override fun deserializeConfig(file: File): GlobalConfig {
        return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file)
    }

    override fun createNewConfig(): GlobalConfig {
        return GlobalConfig()
    }

    override fun copyConfig(config: GlobalConfig): GlobalConfig {
        return config.deepCopy()
    }

}