package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty

data class VldbMetamobConfig(
    @field:JsonProperty var metamobUniqueID: String? = null,
    @field:JsonProperty var metamobPseudo: String? = null,
    @field:JsonProperty var tradeAutoUpdate: Boolean = false,
    @field:JsonProperty var captureAutoUpdate: Boolean = false,
) {
    fun deepCopy(): VldbMetamobConfig {
        return VldbMetamobConfig(metamobUniqueID, metamobPseudo, captureAutoUpdate)
    }
}