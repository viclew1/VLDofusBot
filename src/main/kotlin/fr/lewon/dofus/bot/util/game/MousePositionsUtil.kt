package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.manager.dat.managers.DofusUIPositionsManager
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object MousePositionsUtil {

    private val REF_BANNER_POSITION = PointRelative(0.16957027f, 0.8896952f)
    private val REF_AP_POSITION = PointRelative(0.19628339f, 0.9767779f)
    private val DELTA_AP_POSITION = REF_AP_POSITION.getDifference(REF_BANNER_POSITION)

    fun getRestPosition(gameInfo: GameInfo): PointRelative {
        val context = getContext(gameInfo)
        val uiPosition = DofusUIPositionsManager.getBannerUiPosition(context)
            ?: DefaultUIPositions.BANNER_UI_POSITION
        val uiPositionRelative = ConverterUtil.toPointRelative(uiPosition)
        return uiPositionRelative.getSum(DELTA_AP_POSITION)
    }

    private fun getContext(gameInfo: GameInfo): String {
        return if (isInFight(gameInfo)) {
            DofusUIPositionsManager.CONTEXT_FIGHT
        } else {
            DofusUIPositionsManager.CONTEXT_DEFAULT
        }
    }

    private fun isInFight(gameInfo: GameInfo): Boolean {
        return gameInfo.fightBoard.getEnemyFighters().isNotEmpty()
                || gameInfo.fightBoard.getAlliedFighters().isNotEmpty()
    }

}