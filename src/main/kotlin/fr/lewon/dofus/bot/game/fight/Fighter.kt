package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic

class Fighter(
    var fightCell: FightCell,
    var id: Double,
    var enemy: Boolean
) {
    val statsById: MutableMap<Int, CharacterCharacteristic> = HashMap()
}