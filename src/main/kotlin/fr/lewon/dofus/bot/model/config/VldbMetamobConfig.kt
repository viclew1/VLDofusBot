package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty

data class VldbMetamobConfig(
    @field:JsonProperty var metamobUniqueID: String? = null,
    @field:JsonProperty var metamobPseudo: String? = null,
    @field:JsonProperty var tradeAutoUpdate: Boolean = true,
    @field:JsonProperty var captureAutoUpdate: Boolean = true,
    @field:JsonProperty var shopAutoUpdate: Boolean = true,
) {
    fun deepCopy(): VldbMetamobConfig {
        return VldbMetamobConfig(metamobUniqueID, metamobPseudo, tradeAutoUpdate, captureAutoUpdate, shopAutoUpdate)
    }
}