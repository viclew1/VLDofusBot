package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.math.max
import kotlin.math.min

data class MetamobConfig(
    @field:JsonProperty var metamobUniqueID: String? = null,
    @field:JsonProperty var metamobUsername: String? = null,
    @field:JsonProperty var tradeAutoUpdate: Boolean = true,
    @field:JsonProperty var captureAutoUpdate: Boolean = true,
    @field:JsonProperty var shopAutoUpdate: Boolean = true,
    @field:JsonProperty var simultaneousOchers: Int = 1,
) {

    fun getSafeSimultaneousOchers() = max(1, min(10, simultaneousOchers))
    fun deepCopy(): MetamobConfig {
        return MetamobConfig(
            metamobUniqueID,
            metamobUsername,
            tradeAutoUpdate,
            captureAutoUpdate,
            shopAutoUpdate,
            simultaneousOchers,
        )
    }
}