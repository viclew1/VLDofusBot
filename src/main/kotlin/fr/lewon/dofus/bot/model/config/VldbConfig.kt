package fr.lewon.dofus.bot.model.config

import com.fasterxml.jackson.annotation.JsonProperty

data class VldbConfig(
    @field:JsonProperty var networkInterfaceName: String? = null,
    @field:JsonProperty var playArchMonsterSound: Boolean = true
)