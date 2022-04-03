package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.dat.managers.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.ui.UIBounds
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object ZaapInterfaceUtil {

    private val REF_TOP_LEFT_LOCATION = PointRelative(0.21373057f, 0.10194175f)
    private val REF_HEADER_REGION_BUTTON = PointRelative(0.4804992f, 0.25730994f)
    private val REF_FIRST_ELEMENT_LOCATION = PointRelative(0.43837753f, 0.2962963f)
    private val REF_TENTH_ELEMENT_LOCATION = PointRelative(0.43837753f, 0.64912283f)
    val DELTA_ELEMENT = (REF_TENTH_ELEMENT_LOCATION.y - REF_FIRST_ELEMENT_LOCATION.y) / 9f

    private val REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS = RectangleRelative.build(
        PointRelative(0.75259066f, 0.100323625f),
        PointRelative(0.7797927f, 0.13268608f)
    )
    private val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
    private val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
    private val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
    private val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX

    fun isZaapFrameOpened(gameInfo: GameInfo): Boolean {
        val closeZaapSelectionButtonBounds = getCloseZaapSelectionButtonBounds()
        return ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
                && ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_BG, MAX_COLOR_BG) > 0
    }

    private fun getCloseZaapSelectionButtonBounds(): RectangleRelative {
        return REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS
            .getTranslation(REF_TOP_LEFT_LOCATION.opposite())
            .getTranslation(getZaapTopLeftLocation())
    }

    fun getHeaderRegionButtonLocation(): PointRelative {
        return REF_HEADER_REGION_BUTTON
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    fun getFirstElementLocation(): PointRelative {
        return REF_FIRST_ELEMENT_LOCATION
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    fun getTenthElementLocation(): PointRelative {
        return REF_TENTH_ELEMENT_LOCATION
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    private fun getZaapTopLeftLocation(): PointRelative {
        val zaapUiCenterPoint = DofusUIPositionsManager.getZaapSelectionUiPosition() ?: UIPoint()
        val zaapUiPoint = UIPoint(
            UIBounds.CENTER.x - 750 / 2 + 5 + zaapUiCenterPoint.x,
            UIBounds.CENTER.y - 710 / 2 - 60 + 5 + zaapUiCenterPoint.y
        )
        return ConverterUtil.toPointRelative(zaapUiPoint)
    }

}