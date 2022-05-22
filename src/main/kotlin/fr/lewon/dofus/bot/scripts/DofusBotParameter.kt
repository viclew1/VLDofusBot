package fr.lewon.dofus.bot.scripts

import com.fasterxml.jackson.annotation.JsonProperty

open class DofusBotParameter(
    @field:JsonProperty var key: String = "",
    @field:JsonProperty var description: String = "",
    @field:JsonProperty var value: String = "",
    @field:JsonProperty var type: DofusBotParameterType = DofusBotParameterType.STRING,
    @field:JsonProperty var possibleValues: List<String> = emptyList(),
    @field:JsonProperty var defaultValue: String = value,
)