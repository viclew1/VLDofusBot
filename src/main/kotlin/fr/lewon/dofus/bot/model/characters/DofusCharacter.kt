package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.maps.MapInformation

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClass: DofusClass = DofusClass.values()[0],
    var scriptValues: VldbScriptValues = VldbScriptValues(),
    var zaapDestinations: ArrayList<MapInformation> = ArrayList(),
    var spells: ArrayList<SpellCombination> = ArrayList()
)