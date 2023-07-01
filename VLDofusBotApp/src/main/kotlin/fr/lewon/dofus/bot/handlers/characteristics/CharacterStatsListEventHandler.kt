package fr.lewon.dofus.bot.handlers.characteristics

import fr.lewon.dofus.bot.game.fight.DofusCharacteristicUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.global.CharacterGlobalInformationUIUtil
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.character.stats.CharacterStatsListMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object CharacterStatsListEventHandler : IEventHandler<CharacterStatsListMessage> {

    override fun onEventReceived(socketResult: CharacterStatsListMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val characteristics = socketResult.stats.characteristics
        gameInfo.playerBaseCharacteristics = characteristics.associate {
            it.characteristicId to DofusCharacteristicUtil.getCharacteristicValue(it)
        }
        gameInfo.spellModifications = socketResult.stats.spellModifications
        gameInfo.updatePlayerFighter()
        CharacterGlobalInformationUIUtil.updateCharacterKamas(
            gameInfo.character.name,
            socketResult.stats.kamas.toLong()
        )
    }

}