package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color

object GeneralUIGameUtil {

    private val UI_HP_AP_MP_AREA_VECTOR = PointRelative(0.25482625f, 0.98392284f)
        .getDifference(PointRelative(0.17117117f, 0.8874599f))

    fun isGameReadyToUse(gameInfo: GameInfo): Boolean {
        val uiApMpArea = getUiApMpArea()
        return ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(190, 20, 20), Color(220, 50, 95)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(0, 140, 240), Color(5, 230, 255)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(110, 155, 0), Color(160, 255, 10)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(45, 45, 25), Color(55, 55, 35)) > 0
    }

    fun isInactive(gameInfo: GameInfo): Boolean {
        val uiApMpArea = getUiApMpArea()
        return ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(142, 18, 27), Color(160, 40, 76)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(0, 140, 170), Color(5, 160, 190)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(80, 115, 0), Color(102, 135, 10)) > 0
                && ScreenUtil.colorCount(gameInfo, uiApMpArea, Color(25, 25, 15), Color(35, 35, 25)) > 0
    }

    private fun getUiApMpArea(): RectangleRelative {
        val bannerUiPosition = DofusUIElement.BANNER.getPosition()
        val uiPointRelative = ConverterUtil.toPointRelative(bannerUiPosition)
        return RectangleRelative.build(
            uiPointRelative,
            uiPointRelative.getSum(UI_HP_AP_MP_AREA_VECTOR)
        )
    }
}