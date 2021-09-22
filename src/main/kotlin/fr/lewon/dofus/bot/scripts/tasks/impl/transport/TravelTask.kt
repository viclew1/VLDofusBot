package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.DofusColors
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem
import java.awt.event.KeyEvent

class TravelTask(private val dofusCoordinates: DofusCoordinates) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        if (GameInfo.currentMap.getCoordinates() == dofusCoordinates) {
            return GameInfo.currentMap
        }
        MouseUtil.leftClick(ConfigManager.config.mouseRestPos)
        KeyboardUtil.sendKey(KeyEvent.VK_SPACE, 500)
        KeyboardUtil.writeKeyboard("/travel ${dofusCoordinates.x} ${dofusCoordinates.y}")
        KeyboardUtil.sendKey(KeyEvent.VK_ENTER)
        val okLocation = PointRelative(0.41568628f, 0.5571895f)
        val okButtonDisplayed =
            ScreenUtil.waitForColor(okLocation, DofusColors.VALID_MIN_COLOR, DofusColors.VALID_MAX_COLOR)
        if (!okButtonDisplayed) error("Failed to travel to destination")
        MouseUtil.leftClick(okLocation, false, 0)
        if (!WaitUtil.waitUntil({ GameInfo.currentMap.getCoordinates() == dofusCoordinates }, 300 * 1000)) {
            error("Travel didn't finish in time")
        }
        return GameInfo.currentMap
    }

    override fun onStarted(): String {
        return "Traveling to [${dofusCoordinates.x}; ${dofusCoordinates.y}] ..."
    }

}