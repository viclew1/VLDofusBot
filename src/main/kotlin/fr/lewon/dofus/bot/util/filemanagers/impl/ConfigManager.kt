package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.VldbConfig
import fr.lewon.dofus.bot.util.filemanagers.AbstractConfigManager
import java.io.File


object ConfigManager : AbstractConfigManager<VldbConfig>("config") {

    override fun deserializeConfig(file: File): VldbConfig {
        return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file)
    }

    override fun createNewConfig(): VldbConfig {
        return VldbConfig()
    }

    override fun copyConfig(config: VldbConfig): VldbConfig {
        return config.deepCopy()
    }

}