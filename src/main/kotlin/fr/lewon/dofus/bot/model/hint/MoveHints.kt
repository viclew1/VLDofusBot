package fr.lewon.dofus.bot.model.hint

import com.fasterxml.jackson.annotation.JsonProperty

class MoveHints {
    @JsonProperty
    var hints: ArrayList<Hint> = ArrayList()
}