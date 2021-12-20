package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.util.game.CharacteristicUtil

enum class FighterCharacteristic(val keyword: String) {

    MP("movementPoints"),
    AP("actionPoints"),
    HP("lifePoints"),
    CUR_LIFE("curLife"),
    VITALITY("vitality"),
    RANGE("range");

    fun getFighterCharacteristicValue(fighter: Fighter): Int {
        return CharacteristicUtil.getCharacteristicValue(this, fighter.statsById)
            ?: error("Characteristic not found for fighter [${fighter.id}] : $this")
    }

}