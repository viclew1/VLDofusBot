package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.MetamobConfig
import fr.lewon.dofus.bot.util.filemanagers.AbstractConfigManager
import java.io.File

object MetamobConfigManager : AbstractConfigManager<MetamobConfig>("metamob_config") {

    override fun deserializeConfig(file: File): MetamobConfig {
        return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file)
    }

    override fun createNewConfig(): MetamobConfig {
        return MetamobConfig()
    }

    override fun copyConfig(config: MetamobConfig): MetamobConfig {
        return config.deepCopy()
    }

}