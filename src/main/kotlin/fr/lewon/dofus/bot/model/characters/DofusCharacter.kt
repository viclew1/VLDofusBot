package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClassId: Int = 1,
    var scriptValues: VldbScriptValues = VldbScriptValues(),
    var spells: ArrayList<SpellCombination> = ArrayList(),
    var spellByKey: HashMap<String, DofusSpell> = HashMap()
) {
    @JsonIgnore
    val executionLogger = VldbLogger()

    @JsonIgnore
    val snifferLogger = VldbLogger(logItemCapacity = 100)
}