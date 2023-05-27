package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.basic.TextInformationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class AbstractHavenBagTask(private var shouldReachHavenBag: Boolean) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        gameInfo.eventStore.clear()
        KeyboardUtil.sendKey(gameInfo, 'H', 0)
        if (!WaitUtil.waitUntil({ isToggleHavenBagFinished(gameInfo) })) {
            error("No message arrived in time.")
        }
        if (isToggleHavenBagFail(gameInfo)) {
            gameInfo.logger.addSubLog("Can't reach haven bag on this map.", logItem)
            return false
        }

        val reachedHavenBag = isReachHavenBagSuccess(gameInfo)
        MoveUtil.waitForMapChangeFinished(gameInfo, getComplementaryInformationClass(reachedHavenBag))

        if (shouldReachHavenBag && !reachedHavenBag) {
            gameInfo.logger.addSubLog("Left haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo)
        }
        if (!shouldReachHavenBag && reachedHavenBag) {
            gameInfo.logger.addSubLog("Reached haven bag instead, trying again...", logItem)
            return doExecute(logItem, gameInfo)
        }
        return true
    }

    private fun getComplementaryInformationClass(inHavenBag: Boolean): Class<out MapComplementaryInformationsDataMessage> {
        return if (inHavenBag) {
            MapComplementaryInformationsDataInHavenBagMessage::class.java
        } else {
            MapComplementaryInformationsDataMessage::class.java
        }
    }

    private fun isToggleHavenBagFinished(gameInfo: GameInfo): Boolean {
        return isToggleHavenBagFail(gameInfo) || isReachHavenBagSuccess(gameInfo) || isLeaveHavenBagSuccess(gameInfo)
    }

    private fun isToggleHavenBagFail(gameInfo: GameInfo): Boolean {
        val lastTextInformation = gameInfo.eventStore.getLastEvent(TextInformationMessage::class.java)
        return lastTextInformation != null && lastTextInformation.msgId == 471
    }

    private fun isReachHavenBagSuccess(gameInfo: GameInfo): Boolean {
        return gameInfo.eventStore.getFirstEvent(MapComplementaryInformationsDataInHavenBagMessage::class.java) != null
                && MoveUtil.isMapChanged(gameInfo, getComplementaryInformationClass(true))
    }

    private fun isLeaveHavenBagSuccess(gameInfo: GameInfo): Boolean {
        return gameInfo.eventStore.getFirstEvent(MapComplementaryInformationsDataMessage::class.java) != null
                && MoveUtil.isMapChanged(gameInfo, getComplementaryInformationClass(false))
    }

}

fun main() {
    VldbCoreInitializer.initAll()
    D2OUtil.getObjects("MapPositions").firstOrNull {
        it["id"].toString().toDouble() == 1.861248E8.toDouble()
    }?.let {
        println(it)
        val map = MapManager.getDofusMap(it["id"].toString().toDouble())
        println(map.canReachHavenBag)
        println(map.subArea)
        println(map.name)
    }
}