package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.VldbMetamobConfig
import fr.lewon.dofus.bot.util.filemanagers.AbstractConfigManager
import java.io.File

object MetamobConfigManager : AbstractConfigManager<VldbMetamobConfig>("metamob_config") {

    override fun deserializeConfig(file: File): VldbMetamobConfig {
        return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file)
    }

    override fun createNewConfig(): VldbMetamobConfig {
        return VldbMetamobConfig()
    }

    override fun copyConfig(config: VldbMetamobConfig): VldbMetamobConfig {
        return config.deepCopy()
    }

}