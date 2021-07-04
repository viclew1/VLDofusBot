package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusCoordinate
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.DofusColors
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.event.KeyEvent

class TravelTask(private val dofusCoordinate: DofusCoordinate) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        MouseUtil.leftClick(DTBConfigManager.config.mouseRestPos)
        KeyboardUtil.sendKey(KeyEvent.VK_SPACE, 500)
        KeyboardUtil.writeKeyboard("/travel ${dofusCoordinate.x} ${dofusCoordinate.y}")
        KeyboardUtil.sendKey(KeyEvent.VK_ENTER)
        val okLocation = PointRelative(0.41568628f, 0.5571895f)
        val okButtonDisplayed =
            ScreenUtil.waitForColor(okLocation, DofusColors.VALID_MIN_COLOR, DofusColors.VALID_MAX_COLOR)
        if (!okButtonDisplayed) error("Failed to travel to destination")
        MouseUtil.leftClick(okLocation, false, 0)
        if (!WaitUtil.waitUntil({ GameInfo.currentMap.getCoordinate() == dofusCoordinate }, 300 * 1000)) {
            error("Travel didn't finish in time")
        }
        return GameInfo.currentMap
    }

    override fun onStarted(): String {
        return "Traveling to [${dofusCoordinate.x}; ${dofusCoordinate.y}] ..."
    }

}