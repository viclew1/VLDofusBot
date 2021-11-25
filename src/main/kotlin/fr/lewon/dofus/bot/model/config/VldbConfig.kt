package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty

data class VldbConfig(
    @field:JsonProperty var darkMode: Boolean = false,
    @field:JsonProperty var alwaysOnTop: Boolean = true,
    @field:JsonProperty var logLevel: String = "INFO",
)