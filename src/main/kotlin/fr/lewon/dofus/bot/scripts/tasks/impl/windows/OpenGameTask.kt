package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.game.DefaultUIPositions
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.awt.Color
import java.awt.event.KeyEvent

class OpenGameTask : DofusBotTask<Long>() {

    companion object {
        private val MIDDLE_BOTTOM_LOCATION = PointRelative(0.4967825f, 0.94212216f)
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
        private val UI_HP_AP_MP_AREA_VECTOR = PointRelative(0.25482625f, 0.98392284f)
            .getDifference(PointRelative(0.17117117f, 0.8874599f))

        private val LOGO_SHADOWED_COLOR = Color(74, 33, 0)
        private val LOGO_COLOR = Color(106, 47, 1)
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Long {
        val character = gameInfo.character
        GameSnifferUtil.setGameInfo(character, gameInfo)

        val openGameLogs = VldbLogger.info("Opening game ...", logItem)
        openGame(gameInfo, cancellationToken)
        VldbLogger.closeLog("OK", openGameLogs)

        val loginAreaLogs = VldbLogger.info("Waiting for login area ...", logItem)
        waitForLoginArea(gameInfo, cancellationToken)
        VldbLogger.closeLog("OK", loginAreaLogs)

        val enterLoginLogs = VldbLogger.info("Found login area, entering login and password ...", logItem)
        enterLoginAndPassword(gameInfo, character.login, character.password)
        VldbLogger.closeLog("OK", enterLoginLogs)

        val enterGameLogs = VldbLogger.info("Trying to enter game ... ", logItem)
        val dofusConnection = enterGame(enterGameLogs, gameInfo, cancellationToken)
        enterGameLogs.closeLog("OK")

        return dofusConnection
    }

    private fun enterLoginAndPassword(gameInfo: GameInfo, login: String, password: String) {
        MouseUtil.doubleLeftClick(gameInfo, LOGIN_LOCATION, 200)
        KeyboardUtil.sendSysKey(gameInfo, KeyEvent.VK_BACK_SPACE, 100)
        KeyboardUtil.writeKeyboard(gameInfo, login, 200)
        MouseUtil.doubleLeftClick(gameInfo, PASSWORD_LOCATION, 200)
        KeyboardUtil.sendSysKey(gameInfo, KeyEvent.VK_BACK_SPACE, 100)
        KeyboardUtil.writeKeyboard(gameInfo, password, 200)
        MouseUtil.leftClick(gameInfo, PLAY_BUTTON_BOUNDS.getCenter())
    }

    private fun openGame(gameInfo: GameInfo, cancellationToken: CancellationToken) {
        val pid = JNAUtil.openGame()
        gameInfo.pid = pid
        if (!WaitUtil.waitUntil({ JNAUtil.isGameEnabled(pid) }, cancellationToken)) {
            error("Dofus window isn't enabled")
        }
        val isWindowLoadedFunc = { JNAUtil.getFrameName(pid) == "Dofus" }
        if (!WaitUtil.waitUntil(isWindowLoadedFunc, cancellationToken)) {
            error("Dofus window not found")
        }
        JNAUtil.updateGameBounds(gameInfo, pid)
        MouseUtil.leftClick(gameInfo, MIDDLE_BOTTOM_LOCATION, 200)
        val frameNameValidFunc = { Regex("Dofus [0-9].*?").matches(JNAUtil.getFrameName(pid).trim()) }
        if (!WaitUtil.waitUntil(frameNameValidFunc, cancellationToken)) {
            error("Couldn't open game")
        }
        if (!WaitUtil.waitUntil({ isLogoVisible(gameInfo) }, cancellationToken)) {
            error("Couldn't open game")
        }
    }

    private fun isLogoVisible(gameInfo: GameInfo): Boolean {
        JNAUtil.updateGameBounds(gameInfo, gameInfo.pid)
        return ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0
                || ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_COLOR, LOGO_COLOR) > 0
    }

    private fun waitForLoginArea(gameInfo: GameInfo, cancellationToken: CancellationToken) {
        if (ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0) {
            MouseUtil.leftClick(gameInfo, MIDDLE_BOTTOM_LOCATION, 200)
            KeyboardUtil.sendSysKey(gameInfo, KeyEvent.VK_ESCAPE, 500)
            val colorOkFunc = { ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_COLOR, LOGO_COLOR) > 0 }
            if (!WaitUtil.waitUntil(colorOkFunc, cancellationToken)) {
                error("Couldn't close warning window")
            }
        }
        if (!WaitUtil.waitUntil({ isLoginReady(gameInfo) }, cancellationToken)) {
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

    private fun enterGame(enterGameLogs: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Long {
        if (!WaitUtil.waitUntil({ isAccountLoggedIn(gameInfo) }, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't log in")
        }
        if (isChooseCharacterReady(gameInfo)) {
            VldbLogger.info("Character chosen, entering game.", enterGameLogs)
            MouseUtil.leftClick(gameInfo, CHOOSE_CHARACTER_BUTTON_BOUNDS.getCenter())
        }

        if (!WaitUtil.waitUntil({ isGameFullyLoaded(gameInfo) }, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't enter game")
        }
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 200)

        var pid = -1L
        val isDofusConnectionNotNullFunc = {
            GameSnifferUtil.getCharacterPID(gameInfo.character)?.also { pid = it } != null
        }
        if (!WaitUtil.waitUntil(isDofusConnectionNotNullFunc, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't find dofus connection information")
        }
        return pid
    }

    private fun isAccountLoggedIn(gameInfo: GameInfo): Boolean {
        JNAUtil.updateGameBounds(gameInfo, gameInfo.pid)
        val gameSize = JNAUtil.getGameSize(gameInfo.pid)
        if (gameSize.x <= 0 || gameSize.y <= 0) {
            return false
        }
        return isChooseCharacterReady(gameInfo) || isGameFullyLoaded(gameInfo)
    }

    private fun isChooseCharacterReady(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(
            gameInfo,
            CHOOSE_CHARACTER_BUTTON_BOUNDS,
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0
    }

    private fun isGameFullyLoaded(gameInfo: GameInfo): Boolean {
        val uiPoint = DofusUIPositionsManager.getBannerUiPosition(DofusUIPositionsManager.CONTEXT_DEFAULT)
            ?: DefaultUIPositions.BANNER_UI_POSITION
        val uiPointRelative = ConverterUtil.toPointRelative(uiPoint)
        val uiApMpArea = RectangleRelative.build(
            uiPointRelative,
            uiPointRelative.getSum(UI_HP_AP_MP_AREA_VECTOR)
        )
        return ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(190, 20, 20), Color(220, 50, 95)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(0, 140, 240), Color(5, 230, 255)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(110, 155, 0), Color(160, 255, 10)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(45, 45, 25), Color(55, 55, 35)) > 0
    }

    override fun onStarted(): String {
        val currentCharacter = CharacterManager.getCurrentCharacter() ?: error("No character selected")
        return "Opening game for character [${currentCharacter.pseudo}] ..."
    }

}