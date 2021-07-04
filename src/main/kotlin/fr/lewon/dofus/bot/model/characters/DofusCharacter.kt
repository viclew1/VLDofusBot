package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.game.classes.DofusClass

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClass: DofusClass = DofusClass.values()[0],
    var scriptValues: DTBScriptValues = DTBScriptValues(),
    var zaapDestinations: ArrayList<MapInformation> = ArrayList()
)