package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import fr.lewon.dofus.bot.util.geometry.PointRelative

data class VldbConfig(
    @field:JsonProperty var darkMode: Boolean = false,
    @field:JsonProperty var alwaysOnTop: Boolean = true,
    @field:JsonProperty var logLevel: String = "INFO",
    @field:JsonProperty var globalTimeout: Int = 25,
    @field:JsonProperty var leftAccessPos: PointRelative = PointRelative(0.002f, 0.002f),
    @field:JsonProperty var rightAccessPos: PointRelative = PointRelative(0.995f, 0.115f),
    @field:JsonProperty var bottomAccessPos: PointRelative = PointRelative(0.717f, 0.879f),
    @field:JsonProperty var topAccessPos: PointRelative = PointRelative(0.970f, 0.002f),
    @field:JsonProperty var mouseRestPos: PointRelative = PointRelative(1.2464622f, 0.015852047f),
    @field:JsonProperty var havenBagZaapPos: PointRelative = PointRelative(0.13054188f, 0.48228043f),
    @field:JsonProperty var moveAccessStore: MoveAccessStore = MoveAccessStore()
)