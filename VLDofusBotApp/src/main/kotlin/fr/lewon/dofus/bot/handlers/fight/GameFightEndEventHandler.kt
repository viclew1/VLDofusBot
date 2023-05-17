package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight.GameFightEndMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightResultFighterListEntry
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightResultPlayerListEntry
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object GameFightEndEventHandler : IEventHandler<GameFightEndMessage> {
    override fun onEventReceived(socketResult: GameFightEndMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (MetamobMonstersHelper.isMetamobConfigured() && MetamobConfigManager.readConfig().captureAutoUpdate) {
            updateMetamob(gameInfo, socketResult)
        }
        socketResult.results.filterIsInstance<FightResultPlayerListEntry>()
            .firstOrNull { it.id == gameInfo.playerId }
            ?.let { CharacterGlobalInformationUIUtil.updateCharacterLevel(gameInfo.character.name, it.level) }
    }

    private fun updateMetamob(gameInfo: GameInfo, socketResult: GameFightEndMessage) {
        val playerResult = socketResult.results.firstOrNull {
            it is FightResultPlayerListEntry && it.id == gameInfo.playerId
        } as FightResultPlayerListEntry? ?: return
        val monsterResults = socketResult.results.filterIsInstance<FightResultFighterListEntry>()
            .filter { it !is FightResultPlayerListEntry }
        val monsters = monsterResults
            .mapNotNull { gameInfo.fightBoard.deadFighters.firstOrNull { fighter -> fighter.id == it.id } }
            .filter { it.fighterInfo is GameFightMonsterInformations }
            .map { MonsterManager.getMonster((it.fighterInfo as GameFightMonsterInformations).creatureGenericId.toDouble()) }
        gameInfo.fightBoard.deadFighters.clear()
        Thread {
            MetamobMonstersHelper.addMonsters(playerResult, monsters)
        }.start()
    }

}