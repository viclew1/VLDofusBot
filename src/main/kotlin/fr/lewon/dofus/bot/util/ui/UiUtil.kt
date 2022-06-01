package fr.lewon.dofus.bot.util.ui

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object UiUtil {

    private val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
    private val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
    private val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
    private val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX

    fun isWindowOpenedUsingCloseButton(
        gameInfo: GameInfo,
        uiElement: DofusUIElement,
        fightContext: Boolean = false
    ): Boolean {
        val closeButtonBounds = getContainerBounds(uiElement, "btn_close", fightContext)
        return ScreenUtil.colorCount(gameInfo, closeButtonBounds, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
                && ScreenUtil.colorCount(gameInfo, closeButtonBounds, MIN_COLOR_BG, MAX_COLOR_BG) > 0
    }

    fun getContainerBounds(
        dofusUIElement: DofusUIElement,
        containerName: String,
        fightContext: Boolean = false
    ): RectangleRelative {
        val container = dofusUIElement.getContainer(fightContext).findContainer(containerName)
            ?: error("Couldn't find container : $containerName")
        return ConverterUtil.toRectangleRelative(container.bounds)
    }

}