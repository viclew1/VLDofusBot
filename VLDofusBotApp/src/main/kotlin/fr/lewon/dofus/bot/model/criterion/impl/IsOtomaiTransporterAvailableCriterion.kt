package fr.lewon.dofus.bot.model.criterion.impl

import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.model.criterion.DofusCustomCriterion
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

object IsOtomaiTransporterAvailableCriterion : DofusCustomCriterion() {

    override fun check(characterInfo: DofusCharacterBasicInfo): Boolean =
        CharacterManager.getCharacter(characterInfo.characterName)?.parameters?.isOtomaiTransportAvailable ?: false
}