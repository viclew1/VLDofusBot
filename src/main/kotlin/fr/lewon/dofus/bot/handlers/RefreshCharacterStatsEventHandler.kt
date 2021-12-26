package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.fight.FighterCharacteristic
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.RefreshCharacterStatsMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.game.CharacteristicUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import kotlin.math.min

object RefreshCharacterStatsEventHandler : EventHandler<RefreshCharacterStatsMessage> {

    override fun onEventReceived(socketResult: RefreshCharacterStatsMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val fighterId = socketResult.fighterId
        val fighter = gameInfo.fightBoard.getFighterById(fighterId)
        val characteristics = socketResult.stats.characteristics.characteristics
        gameInfo.fightBoard.updateFighterCharacteristics(fighterId, characteristics)
        if (fighter != null) {
            val hp = CharacteristicUtil.getCharacteristicValue(FighterCharacteristic.HP, fighter.statsById)
            val vitality = CharacteristicUtil.getCharacteristicValue(FighterCharacteristic.VITALITY, fighter.statsById)
            val curLife = CharacteristicUtil.getCharacteristicValue(FighterCharacteristic.CUR_LIFE, fighter.statsById)
            if (hp != null && vitality != null && curLife != null) {
                fighter.maxHp = hp + vitality
                fighter.hp = min(fighter.maxHp, fighter.maxHp - fighter.hpLost + fighter.hpHealed + curLife)
            }
        }
        gameInfo.logger.debug("Fighter [$fighterId] characteristics updated")
    }
}