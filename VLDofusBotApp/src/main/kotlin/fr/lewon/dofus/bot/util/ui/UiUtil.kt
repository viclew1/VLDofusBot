package fr.lewon.dofus.bot.util.ui

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.toRectangleRelative
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Color

object UiUtil {

    /**
     * Checks if the passed ui element is opened by checking if its close button is visible
     */
    fun isUiElementWindowOpened(
        gameInfo: GameInfo,
        uiElement: DofusUIElement,
        fightContext: Boolean = false,
        closeButtonBackgroundColorMin: Color = DofusColors.UI_BANNER_GREY_COLOR_MIN,
        closeButtonBackgroundColorMax: Color = DofusColors.UI_BANNER_GREY_COLOR_MAX,
        closeButtonCrossColorMin: Color = DofusColors.UI_BANNER_BLACK_COLOR_MIN,
        closeButtonCrossColorMax: Color = DofusColors.UI_BANNER_BLACK_COLOR_MAX,
    ): Boolean {
        val closeButtonBounds = getContainerBounds(uiElement, "btn_close", fightContext)
        return ScreenUtil.colorCount(
            gameInfo,
            closeButtonBounds,
            closeButtonCrossColorMin,
            closeButtonCrossColorMax
        ) > 0 && ScreenUtil.colorCount(
            gameInfo,
            closeButtonBounds,
            closeButtonBackgroundColorMin,
            closeButtonBackgroundColorMax
        ) > 0
    }

    fun closeWindow(gameInfo: GameInfo, uiElement: DofusUIElement, fightContext: Boolean = false) {
        MouseUtil.leftClick(gameInfo, getContainerBounds(uiElement, "btn_close", fightContext).getCenter())
    }

    fun getContainerBounds(
        dofusUIElement: DofusUIElement,
        containerName: String,
        fightContext: Boolean = false
    ): RectangleRelative {
        val container = dofusUIElement.getContainer(fightContext).findContainer(containerName)
            ?: error("Couldn't find container : $containerName")
        return container.bounds.toRectangleRelative()
    }

}