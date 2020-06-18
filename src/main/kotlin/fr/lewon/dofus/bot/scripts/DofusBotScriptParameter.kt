package fr.lewon.dofus.bot.scripts

import com.fasterxml.jackson.annotation.JsonProperty

class DofusBotScriptParameter(
    @field:JsonProperty var key: String = "",
    @field:JsonProperty var description: String = "",
    @field:JsonProperty var value: String = ""
)