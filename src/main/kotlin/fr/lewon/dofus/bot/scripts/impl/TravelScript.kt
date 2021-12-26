package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelToCoordinatesTask
import fr.lewon.dofus.bot.util.network.GameInfo

class TravelScript : DofusBotScript("Travel") {

    private val xParameter = DofusBotParameter(
        "x", "X coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    private val yParameter = DofusBotParameter(
        "y", "Y coordinates of destination", "0", DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            xParameter,
            yParameter
        )
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Does the same as the autopilot mount, except you don't need to have an autopilot mount."
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val travelOk = TravelToCoordinatesTask(DofusCoordinates(xParameter.value.toInt(), yParameter.value.toInt()))
            .run(logItem, gameInfo)
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

}