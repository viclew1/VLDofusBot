package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameEntitiesDispositionMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class FightMonsterGroupTask(
    private val preferredEntityId: Double? = null,
    private val stopIfNoMonsterPresent: Boolean = false
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val couldStartFight = RetryUtil.tryUntilSuccess({ tryToStartFight(gameInfo) }, 20, {
            if (stopIfNoMonsterPresent && gameInfo.monsterInfoByEntityId.isEmpty()) {
                error("No monster on map")
            }
            WaitUtil.waitUntil({ gameInfo.monsterInfoByEntityId.isNotEmpty() }, 60000)
        })
        if (!couldStartFight) {
            error("Couldn't start a fight")
        }
        return FightTask().run(logItem, gameInfo)
    }

    private fun tryToStartFight(gameInfo: GameInfo): Boolean {
        val monsterEntityId = preferredEntityId
            ?: gameInfo.monsterInfoByEntityId.keys.firstOrNull()
            ?: return false
        val monsterCellId = gameInfo.entityPositionsOnMapByEntityId[monsterEntityId]
            ?: return false
        val clickPosition = InteractiveUtil.getCellClickPosition(gameInfo, monsterCellId, false)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 400)
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, clickPosition)
        WaitUtil.waitUntil(
            {
                gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null
                        || gameInfo.entityPositionsOnMapByEntityId[monsterEntityId] != monsterCellId
            }, 8000
        )
        return if (gameInfo.entityPositionsOnMapByEntityId[monsterEntityId] != monsterCellId) {
            WaitUtil.waitUntil(
                { gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null }, 8000
            )
        } else {
            gameInfo.eventStore.getLastEvent(GameEntitiesDispositionMessage::class.java) != null
        }
    }

    override fun onStarted(): String {
        return "Fighting any monster group ... "
    }
}