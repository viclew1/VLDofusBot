package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object RetryUtil {

    fun <T> tryUntilSuccess(
        function: () -> T?,
        successChecker: (T?) -> Boolean,
        tryCount: Int,
        millisBetweenTries: Int = 0
    ): T? {
        var currentTryCount = 0
        while (currentTryCount++ < tryCount) {
            val result = function()
            if (successChecker(result)) {
                return result
            }
            WaitUtil.sleep(millisBetweenTries)
        }
        return null
    }

    fun tryUntilSuccess(
        function: () -> Boolean,
        tryCount: Int,
        millisBetweenTries: Int = 0
    ): Boolean {
        var currentTryCount = 0
        while (currentTryCount++ < tryCount) {
            if (function()) {
                return true
            }
            WaitUtil.sleep(millisBetweenTries)
        }
        return false
    }

    fun clickUntil(
        gameInfo: GameInfo,
        location: PointRelative,
        successChecker: () -> Boolean,
        tryCount: Int,
        millisBetweenTries: Int = 0
    ): Boolean {
        var currentTryCount = 0
        while (currentTryCount++ < tryCount) {
            MouseUtil.leftClick(gameInfo, location, moveBeforeClick = false)
            if (successChecker()) {
                return true
            }
            WaitUtil.sleep(millisBetweenTries)
        }
        return false
    }

}