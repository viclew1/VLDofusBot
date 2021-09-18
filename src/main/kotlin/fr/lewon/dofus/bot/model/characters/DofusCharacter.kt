package fr.lewon.dofus.bot.model.characters

data class DofusCharacter(
    var login: String = "",
    var password: String = "",
    var pseudo: String = "",
    var dofusClass: DofusClass = DofusClass.values()[0],
    var scriptValues: DTBScriptValues = DTBScriptValues(),
    var zaapDestinations: ArrayList<MapInformation> = ArrayList()
)