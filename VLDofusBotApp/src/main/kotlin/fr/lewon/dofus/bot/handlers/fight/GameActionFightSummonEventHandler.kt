package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.actions.fight.GameActionFightSummonMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameActionFightSummonEventHandler : IEventHandler<GameActionFightSummonMessage> {
    override fun onEventReceived(socketResult: GameActionFightSummonMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        for (summon in socketResult.summons) {
            val fighter = gameInfo.fightBoard.createOrUpdateFighter(summon)
            gameInfo.fightBoard.updateFighterCharacteristics(fighter, summon.stats.characteristics.characteristics)
            val hp = DofusCharacteristics.LIFE_POINTS.getValue(fighter) +
                    DofusCharacteristics.VITALITY.getValue(fighter)
            fighter.maxHp = hp
            fighter.baseHp = hp
        }
    }
}