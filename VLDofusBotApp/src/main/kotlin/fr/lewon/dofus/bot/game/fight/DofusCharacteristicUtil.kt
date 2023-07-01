package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristicDetailed
import fr.lewon.dofus.bot.sniffer.model.types.game.character.characteristic.CharacterCharacteristicValue

object DofusCharacteristicUtil {

    fun getCharacteristicValue(characteristic: CharacterCharacteristic): Int {
        return when (characteristic) {
            is CharacterCharacteristicDetailed -> characteristic.base + characteristic.additional + characteristic.contextModif + characteristic.objectsAndMountBonus + characteristic.alignGiftBonus
            is CharacterCharacteristicValue -> characteristic.total
            else -> error("Untreated characteristic type : ${characteristic::class.java.typeName}")
        }
    }

}