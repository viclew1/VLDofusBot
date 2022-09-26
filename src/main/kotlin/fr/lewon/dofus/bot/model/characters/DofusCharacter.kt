package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.logs.VldbLogger

data class DofusCharacter(
    val pseudo: String = "",
    val dofusClassId: Int = 1,
) {

    @JsonIgnore
    val executionLogger = VldbLogger()

    @JsonIgnore
    val snifferLogger = VldbLogger(100)

}