package fr.lewon.dofus.bot.scripts.tasks.impl.windows

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.game.DefaultUIPositions
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.awt.Color
import java.awt.event.KeyEvent

class OpenGameTask : DofusBotTask<Long>() {

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
        private val UI_HP_AP_MP_AREA_VECTOR = PointRelative(0.25482625f, 0.98392284f)
            .getDifference(PointRelative(0.17117117f, 0.8874599f))

        private val LOGO_SHADOWED_COLOR = Color(74, 33, 0)
        private val LOGO_COLOR = Color(106, 47, 1)
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Long {
        val character = gameInfo.character
        GameSnifferUtil.setGameInfo(character, gameInfo)

        val bannerUiPosition = DofusUIPositionsManager.getBannerUiPosition(DofusUIPositionsManager.CONTEXT_DEFAULT)
            ?: DefaultUIPositions.BANNER_UI_POSITION

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
        val pid = enterGame(enterGameLogs, gameInfo, cancellationToken, bannerUiPosition)
        enterGameLogs.closeLog("OK")

        return pid
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
        JNAUtil.updateGameBounds(gameInfo)
        if (!WaitUtil.waitUntil({ isFrameValid(gameInfo) }, cancellationToken)
            || !WaitUtil.waitUntil({ isLogoVisible(gameInfo) }, cancellationToken)
        ) {
            error("Couldn't open game")
        }
    }

    private fun isFrameValid(gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, BOTTOM_MIDDLE_POINT)
        val frameName = JNAUtil.getFrameName(gameInfo.pid)
        return Regex("Dofus [0-9].*?").matches(frameName.trim())
    }

    private fun isLogoVisible(gameInfo: GameInfo): Boolean {
        JNAUtil.updateGameBounds(gameInfo)
        return ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0
                || ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_COLOR, LOGO_COLOR) > 0
    }

    private fun waitForLoginArea(gameInfo: GameInfo, cancellationToken: CancellationToken) {
        if (ScreenUtil.colorCount(gameInfo, LOGO_BOUNDS, LOGO_SHADOWED_COLOR, LOGO_SHADOWED_COLOR) > 0) {
            MouseUtil.leftClick(gameInfo, BOTTOM_MIDDLE_POINT, 200)
            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_ESCAPE, 500)
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

    private fun enterGame(
        enterGameLogs: LogItem,
        gameInfo: GameInfo,
        cancellationToken: CancellationToken,
        bannerUiPosition: UIPoint
    ): Long {
        if (!WaitUtil.waitUntil({ isAccountLoggedIn(gameInfo, bannerUiPosition) }, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't log in")
        }
        if (!isGameFullyLoaded(gameInfo, bannerUiPosition)) {
            VldbLogger.info("Character chosen, entering game.", enterGameLogs)
            MouseUtil.leftClick(gameInfo, CHOOSE_CHARACTER_BUTTON_BOUNDS.getCenter())
        }

        if (!WaitUtil.waitUntil({ isGameFullyLoaded(gameInfo, bannerUiPosition) }, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't enter game")
        }
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))

        var pid = -1L
        val isDofusConnectionNotNullFunc =
            { GameSnifferUtil.getCharacterPID(gameInfo.character)?.also { pid = it } != null }
        if (!WaitUtil.waitUntil(isDofusConnectionNotNullFunc, cancellationToken, 1000 * 3 * 60)) {
            error("Couldn't find dofus connection information")
        }
        return pid
    }

    private fun isAccountLoggedIn(gameInfo: GameInfo, bannerUiPosition: UIPoint): Boolean {
        JNAUtil.updateGameBounds(gameInfo)
        MouseUtil.leftClick(gameInfo, BOTTOM_MIDDLE_POINT)
        return isChooseCharacterReady(gameInfo) || isGameFullyLoaded(gameInfo, bannerUiPosition)
    }

    private fun isChooseCharacterReady(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(
            gameInfo,
            CHOOSE_CHARACTER_BUTTON_BOUNDS,
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0
    }

    private fun isGameFullyLoaded(gameInfo: GameInfo, bannerUiPosition: UIPoint): Boolean {
        val uiPointRelative = ConverterUtil.toPointRelative(bannerUiPosition)
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