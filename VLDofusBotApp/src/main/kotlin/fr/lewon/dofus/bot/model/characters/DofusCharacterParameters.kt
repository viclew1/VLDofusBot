package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.scripts.parameters.impl.BooleanParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.ChoiceParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

data class DofusCharacterParameters(
    var isOtomaiTransportAvailable: Boolean = true,
    var isFrigost2Available: Boolean = true,
    var harvestableSet: String = HarvestableSetsManager.defaultHarvestableIdsBySetName.keys.first(),
    var minAvailableWeight: Int = 300,
)

val HarvestableSetParameter = ChoiceParameter(
    "Harvestable set",
    "The harvestable set which will be used for this character in scripts execution",
    defaultValue = HarvestableSetsManager.getHarvestableIdsBySetName().keys.first(),
    getAvailableValues = { HarvestableSetsManager.getHarvestableIdsBySetName().keys.toList() },
    itemValueToString = { it },
    stringToItemValue = { it }
)

val IsOtomaiTransportAvailableParameter = BooleanParameter(
    "Otomai Transporter",
    "Can use Otomai island transporters",
    true
)

val IsFrigost2AvailableParameter = BooleanParameter(
    "Frigost 2 Zaap",
    "Has access to the Frigost 2 zaap \"Village Enseveli\"",
    true
)

val MinAvailableWeightParameter = IntParameter(
    "Minimum available weight",
    "The character will drop all of its resources when its remaining weight is lower than the minimum available weight",
    300
)