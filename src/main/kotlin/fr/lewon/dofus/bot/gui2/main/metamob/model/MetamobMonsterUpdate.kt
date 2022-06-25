package fr.lewon.dofus.bot.gui2.main.metamob.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MetamobMonsterUpdate(
    var id: Int = 0,
    @field:JsonProperty("etat") var state: MetamobMonsterUpdateState = MetamobMonsterUpdateState.NONE,
    @field:JsonProperty("quantite") var amount: String = "0"
)