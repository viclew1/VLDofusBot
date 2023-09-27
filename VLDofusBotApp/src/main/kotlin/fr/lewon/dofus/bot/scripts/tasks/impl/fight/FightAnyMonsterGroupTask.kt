package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.exceptions.DofusBotTaskFatalException
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class FightAnyMonsterGroupTask(
    private val fightArchmonsters: Boolean = false,
    private val fightQuestMonsters: Boolean = false,
    private val stopIfNoMonsterPresent: Boolean = false,
    private val maxMonsterGroupLevel: Int = 0,
    private val maxMonsterGroupSize: Int = 0
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        val validMonsterEntityIds = gameInfo.monsterInfoByEntityId.filter { entry ->
            val mainMonster = gameInfo.mainMonstersByGroupOnMap[entry.value]
            val groupLevel = entry.value.staticInfos.underlings.sumOf { monster -> monster.level } +
                entry.value.staticInfos.mainCreatureLightInfos.level
            val groupSize = entry.value.staticInfos.underlings.size + 1
            mainMonster != null
                && (!mainMonster.isMiniBoss || fightArchmonsters)
                && (!mainMonster.isQuestMonster || fightQuestMonsters)
                && (maxMonsterGroupLevel <= 0 || groupLevel <= maxMonsterGroupLevel)
                && (maxMonsterGroupSize <= 0 || groupSize <= maxMonsterGroupSize)
        }.keys.toList()
        if (validMonsterEntityIds.isEmpty()) {
            return false
        }
        val couldStartFight = RetryUtil.tryUntilSuccess({ tryToStartFight(gameInfo, validMonsterEntityIds) }, 20, {
            if (stopIfNoMonsterPresent && gameInfo.monsterInfoByEntityId.isEmpty()) {
                error("No monster on map")
            }
            WaitUtil.waitUntil(60000) { gameInfo.monsterInfoByEntityId.isNotEmpty() }
        })
        if (!couldStartFight) {
            error("Couldn't start a fight")
        }
        if (!FightTask().run(logItem, gameInfo)) {
            throw DofusBotTaskFatalException("Failed to fight")
        }
        return true
    }

    private fun tryToStartFight(gameInfo: GameInfo, monsterEntityIds: List<Double>): Boolean {
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