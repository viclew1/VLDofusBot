package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.GeneralUIGameUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.awt.Color
import java.awt.event.KeyEvent

class OpenGameTask : DofusBotTask<DofusConnection>() {

    companion object {
        private val BOTTOM_MIDDLE_POINT = PointRelative(0.5f, 0.95f)
        private val LOGO_BOUNDS = RectangleRelative.build(
            PointRelative(0.47625f, 0.1875f),
            PointRelative(0.49125f, 0.20625f)
        )
        private val PLAY_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.43375f, 0.540625f),
            PointRelative(0.46375f, 0.5625f)
        )
        private val LOGIN_LOCATION = PointRelative(0.4015444f, 0.31993568f)
        private val PASSWORD_LOCATION = PointRelative(0.4002574f, 0.37942123f)
        private val CHOOSE_CHARACTER_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.57400256f, 0.74437296f),
            PointRelative(0.7992278f, 0.8102894f)
        )

        private val LOGO_SHADOWED_COLOR = Color(74, 33, 0)
        private val LOGO_COLOR = Color(106, 47, 1)
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo): DofusConnection {
        val character = gameInfo.character

        val openGameLogs = gameInfo.logger.addSubLog("Opening game ...", logItem)
        openGame(gameInfo)
        gameInfo.logger.closeLog("OK", openGameLogs, true)

        val loginAreaLogs = gameInfo.logger.addSubLog("Waiting for login area ...", logItem)
        waitForLoginArea(gameInfo)
        gameInfo.logger.closeLog("OK", loginAreaLogs, true)

        val enterLoginLogs = gameInfo.logger.addSubLog("Found login area, entering login and password ...", logItem)
        enterLoginAndPassword(gameInfo, character.login, character.password)
        gameInfo.logger.closeLog("OK", enterLoginLogs, true)

        val enterGameLogs = gameInfo.logger.addSubLog("Trying to enter game ... ", logItem)
        val connection = enterGame(enterGameLogs, gameInfo)
        gameInfo.logger.closeLog("OK", enterGameLogs, true)

        return connection
    }

    private fun enterLoginAndPassword(gameInfo: GameInfo, login: String, password: String) {
        MouseUtil.tripleLeftClick(gameInfo, LOGIN_LOCATION)
        KeyboardUtil.writeKeyboard(gameInfo, login)
        MouseUtil.leftClick(gameInfo, BOTTOM_MIDDLE_POINT)
        MouseUtil.tripleLeftClick(gameInfo, PASSWORD_LOCATION)
        KeyboardUtil.writeKeyboard(gameInfo, password)
        MouseUtil.leftClick(gameInfo, BOTTOM_MIDDLE_POINT)
        MouseUtil.leftClick(gameInfo, PLAY_BUTTON_BOUNDS.getCenter())
    }

    private fun openGame(gameInfo: GameInfo) {
        val pid = JNAUtil.openGame()
        gameInfo.connection = DofusConnection("", "", "", "", pid)
        if (!WaitUtil.waitUntil({ JNAUtil.isGameEnabled(pid) })) {
            error("Dofus window isn't enabled")
        }
        JNAUtil.updateGameBounds(gameInfo)
        if (!WaitUtil.waitUntil({ JNAUtil.getFrameName(pid) == "Dofus" })) {
            error("Dofus window not found")
        }
        if (!WaitUtil.waitUntil({ isLogoVisible(gameInfo) })) {
            error("Couldn't open game")
        }
    }

    private fun isLogoVisible(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_COLOR, LOGO_COLOR) > 0
                || ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0
    }

    private fun waitForLoginArea(gameInfo: GameInfo) {
        if (ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0) {
            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_ESCAPE, 500)
            val colorOkFunc = { ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_COLOR, LOGO_COLOR) > 0 }
            if (!WaitUtil.waitUntil(colorOkFunc)) {
                error("Couldn't close warning window")
            }
        }
        if (!WaitUtil.waitUntil({ isLoginReady(gameInfo) })) {
            error("Couldn't find login area")
        }
    }

    private fun isLoginReady(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(
            gameInfo,
            PLAY_BUTTON_BOUNDS,
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0
    }

    private fun enterGame(enterGameLogs: LogItem, gameInfo: GameInfo): DofusConnection {
        if (!WaitUtil.waitUntil({ isAccountLoggedIn(gameInfo) }, 1000 * 3 * 60)) {
            error("Couldn't log in")
        }
        if (!GeneralUIGameUtil.isGameReadyToUse(gameInfo)) {
            gameInfo.logger.addSubLog("Character chosen, entering game.", enterGameLogs)
            MouseUtil.leftClick(gameInfo, CHOOSE_CHARACTER_BUTTON_BOUNDS.getCenter())
        }

        if (!WaitUtil.waitUntil({ GeneralUIGameUtil.isGameReadyToUse(gameInfo) }, 1000 * 3 * 60)) {
            error("Couldn't enter game")
        }
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        if (!WaitUtil.waitUntil({ isDofusConnectionNotNull(gameInfo) }, 1000 * 3 * 60)) {
            error("Couldn't find dofus connection information")
        }
        val connection = GameSnifferUtil.getConnection(gameInfo.character)
            ?: error("No connection found")

        GameSnifferUtil.setGameInfo(connection, gameInfo)
        return gameInfo.connection
    }

    private fun isAccountLoggedIn(gameInfo: GameInfo): Boolean {
        return isChooseCharacterReady(gameInfo) || GeneralUIGameUtil.isGameReadyToUse(gameInfo)
    }

    private fun isChooseCharacterReady(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(
            gameInfo,
            CHOOSE_CHARACTER_BUTTON_BOUNDS,
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0
    }

    private fun isDofusConnectionNotNull(gameInfo: GameInfo): Boolean {
        GameSnifferUtil.updateNetwork()
        return GameSnifferUtil.getConnection(gameInfo.character) != null
    }

    override fun onStarted(): String {
        return "Opening game ..."
    }

}