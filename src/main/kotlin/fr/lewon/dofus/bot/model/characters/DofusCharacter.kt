package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell

data class DofusCharacter(
    var pseudo: String = "",
    var dofusClassId: Int = 1,
    var scriptValuesStore: VldbScriptValuesStore = VldbScriptValuesStore(),
    var characterSpells: ArrayList<CharacterSpell> = ArrayList()
) {

    @JsonIgnore
    val executionLogger = VldbLogger()

    @JsonIgnore
    val snifferLogger = VldbLogger(100)

}