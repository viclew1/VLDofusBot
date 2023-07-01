package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.toPointRelative
import fr.lewon.dofus.bot.util.network.info.GameInfo

object MousePositionsUtil {

    private val REF_BANNER_POSITION = PointRelative(0.16957027f, 0.8896952f)
    private val REF_AP_POSITION = PointRelative(0.19628339f, 0.9767779f)
    private val DELTA_AP_POSITION = REF_AP_POSITION.getDifference(REF_BANNER_POSITION)

    fun getRestPosition(gameInfo: GameInfo): PointRelative {
        val uiPosition = DofusUIElement.BANNER.getPosition(isInFight(gameInfo))
        val uiPositionRelative = uiPosition.toPointRelative()
        return uiPositionRelative.getSum(DELTA_AP_POSITION)
    }

    private fun isInFight(gameInfo: GameInfo): Boolean {
        return gameInfo.fightBoard.getEnemyFighters().isNotEmpty()
                || gameInfo.fightBoard.getAlliedFighters().isNotEmpty()
    }

}