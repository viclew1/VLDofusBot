package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.logs.VldbLogger

data class DofusCharacter(
    var name: String = "",
    var dofusClassId: Int = 1,
    var isOtomaiTransportAvailable: Boolean = true
) {

    @JsonIgnore
    val executionLogger = VldbLogger()

    @JsonIgnore
    val snifferLogger = VldbLogger()

}