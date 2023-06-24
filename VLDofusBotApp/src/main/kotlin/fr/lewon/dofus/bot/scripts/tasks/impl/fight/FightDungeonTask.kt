package fr.lewon.dofus.bot.scripts.tasks.impl.fight

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.dungeon.Dungeon
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.GameRolePlayShowActorMessage
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class FightDungeonTask(private val dungeon: Dungeon, private val shouldExit: Boolean = true) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (!ReachMapTask(listOf(dungeon.map)).run(logItem, gameInfo)) {
            return false
        }
        NpcSpeakTask(dungeon.enterNpcId, dungeon.enterDialogIds).run(logItem, gameInfo)
        MoveUtil.waitForMapChangeFinished(gameInfo)
        while (!gameInfo.entityIdByNpcId.keys.contains(dungeon.exitNpcId)) {
            val monsterGroupPassed = RetryUtil.tryUntilSuccess(
                { FightMonsterGroupTask().run(logItem, gameInfo) },
                20,
                { WaitUtil.waitUntil { gameInfo.eventStore.getLastEvent(GameRolePlayShowActorMessage::class.java) != null } }
            )
            if (!monsterGroupPassed) {
                return false
            }
        }
        if (shouldExit) {
            WaitUtil.sleep(2000)
            gameInfo.eventStore.clear()
            NpcSpeakTask(dungeon.exitNpcId, dungeon.exitDialogIds).run(logItem, gameInfo)
            MoveUtil.waitForMapChangeFinished(gameInfo)
        }
        return true
    }

    override fun onStarted(): String {
        return "Fighting dungeon ..."
    }

}