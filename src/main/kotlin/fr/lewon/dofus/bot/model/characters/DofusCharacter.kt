package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClass: DofusClass = DofusClass.values()[0],
    var scriptValues: VldbScriptValues = VldbScriptValues(),
    var spells: ArrayList<SpellCombination> = ArrayList()
)