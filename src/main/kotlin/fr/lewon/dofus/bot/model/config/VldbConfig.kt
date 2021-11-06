package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import fr.lewon.dofus.bot.util.geometry.PointRelative

data class VldbConfig(
    @field:JsonProperty var darkMode: Boolean = false,
    @field:JsonProperty var alwaysOnTop: Boolean = true,
    @field:JsonProperty var logLevel: String = "INFO",
    @field:JsonProperty var globalTimeout: Int = 25,
    @field:JsonProperty var mouseRestPos: PointRelative = PointRelative(1.2464622f, 0.015852047f),
)