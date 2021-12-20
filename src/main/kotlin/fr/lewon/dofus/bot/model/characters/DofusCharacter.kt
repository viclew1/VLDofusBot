package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClass: DofusClass = DofusClass.values()[0],
    var scriptValues: VldbScriptValues = VldbScriptValues(),
    var spells: ArrayList<SpellCombination> = ArrayList()
) {
    @JsonIgnore
    val executionLogger = VldbLogger()

    @JsonIgnore
    val snifferLogger = VldbLogger(logItemCapacity = 100)
}