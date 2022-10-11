package fr.lewon.dofus.bot.handlers.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightEndMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.result.entry.FightResultFighterListEntry
import fr.lewon.dofus.bot.sniffer.model.types.fight.result.entry.FightResultPlayerListEntry
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersUpdater
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object GameFightEndEventHandler : IEventHandler<GameFightEndMessage> {
    override fun onEventReceived(socketResult: GameFightEndMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        if (MetamobConfigManager.readConfig().captureAutoUpdate) {
            updateMetamob(gameInfo, socketResult)
        }
        socketResult.results.filterIsInstance<FightResultPlayerListEntry>()
            .firstOrNull { it.id == gameInfo.playerId }
            ?.let {
                CharacterGlobalInformationUIUtil.updateCharacterLevel(gameInfo.character.name, it.level)
            }
    }

    private fun updateMetamob(gameInfo: GameInfo, socketResult: GameFightEndMessage) {
        val playerResult = socketResult.results.firstOrNull {
            it is FightResultPlayerListEntry && it.id == gameInfo.playerId
        } ?: return
        val monsterResults = socketResult.results.filterIsInstance<FightResultFighterListEntry>()
            .filter { it !is FightResultPlayerListEntry }
        val monsters = monsterResults
            .mapNotNull { gameInfo.fightBoard.deadFighters.firstOrNull { fighter -> fighter.id == it.id } }
            .filter { it.fighterInfo is GameFightMonsterInformations }
            .map { MonsterManager.getMonster((it.fighterInfo as GameFightMonsterInformations).creatureGenericId.toDouble()) }
        gameInfo.fightBoard.deadFighters.clear()
        if (MetamobMonstersUpdater.isMetamobConfigured()) {
            Thread {
                MetamobMonstersUpdater.addMonsters(playerResult as FightResultPlayerListEntry, monsters)
            }.start()
        }
    }

}