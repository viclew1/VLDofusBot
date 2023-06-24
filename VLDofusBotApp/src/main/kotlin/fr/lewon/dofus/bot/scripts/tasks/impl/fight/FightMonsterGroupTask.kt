package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class FightMonsterGroupTask(
    private val preferredEntityId: Double? = null,
    private val stopIfNoMonsterPresent: Boolean = false
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        val couldStartFight = RetryUtil.tryUntilSuccess({ tryToStartFight(gameInfo) }, 20, {
            if (stopIfNoMonsterPresent && gameInfo.monsterInfoByEntityId.isEmpty()) {
                error("No monster on map")
            }
            WaitUtil.waitUntil(60000) { gameInfo.monsterInfoByEntityId.isNotEmpty() }
        })
        if (!couldStartFight) {
            error("Couldn't start a fight")
        }
        return FightTask().run(logItem, gameInfo)
    }

    private fun tryToStartFight(gameInfo: GameInfo): Boolean {
        val monsterEntityIds = preferredEntityId?.let { listOf(it) }
            ?: gameInfo.monsterInfoByEntityId.keys
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player")
        val distanceByEntityId = HashMap<Double, Int>()
        for (entityId in monsterEntityIds) {
            val cellId = gameInfo.entityPositionsOnMapByEntityId[entityId] ?: continue
            val distance = gameInfo.dofusBoard.getPathLength(playerCellId, cellId) ?: continue
            distanceByEntityId[entityId] = distance
        }
        val monsterEntityId = distanceByEntityId.minByOrNull { it.value }?.key ?: return false
        val monsterCellId = gameInfo.entityPositionsOnMapByEntityId[monsterEntityId] ?: return false
        val clickPosition = InteractiveUtil.getCellClickPosition(gameInfo, monsterCellId, false)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        MouseUtil.leftClick(gameInfo, clickPosition)
        WaitUtil.waitUntil(8000) {
            gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null
                    || gameInfo.entityPositionsOnMapByEntityId[monsterEntityId] != monsterCellId
        }
        return WaitUtil.waitUntil(1200) {
            gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null
        }
    }

    override fun onStarted(): String {
        return "Fighting any monster group ... "
    }
}