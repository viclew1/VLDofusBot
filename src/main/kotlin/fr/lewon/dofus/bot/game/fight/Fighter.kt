package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic

class Fighter(
    var cell: DofusCell,
    var id: Double,
    var enemy: Boolean
) {
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()
}