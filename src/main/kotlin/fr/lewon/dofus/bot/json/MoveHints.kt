package fr.lewon.dofus.bot.json

import com.fasterxml.jackson.annotation.JsonProperty

class MoveHints {
    @JsonProperty
    var position: Position? = null
    @JsonProperty
    var hints: List<Hint> = emptyList()
}

class Position {
    @JsonProperty
    var x = 0
    @JsonProperty
    var y = 0
    @JsonProperty
    var dir = ""
}

class Hint {
    @JsonProperty
    var x = 0
    @JsonProperty
    var y = 0
    @JsonProperty
    var n = ""
    @JsonProperty
    var d = -1
}