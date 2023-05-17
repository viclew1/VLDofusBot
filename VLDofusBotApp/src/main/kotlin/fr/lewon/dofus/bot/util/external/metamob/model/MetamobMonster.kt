package fr.lewon.dofus.bot.util.external.metamob.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MetamobMonster(
    var id: Int = 0,
    @field:JsonProperty("nom") var name: String = "",
    var type: MetamobMonsterType = MetamobMonsterType.MONSTER,
    @field:JsonProperty("etape") var step: Int = 0,
    @field:JsonProperty("zone") var area: String = "",
    @field:JsonProperty("souszone") var subArea: String = "",
    @field:JsonProperty("quantite") var amount: Int = 0,
    @field:JsonProperty("recherche") var searched: Int = 0,
    @field:JsonProperty("propose") var offered: Int = 0,
    @field:JsonProperty("nom_normal") var normalName: String = "",
    @field:JsonProperty("image_url") var imageUrl: String = "",
)