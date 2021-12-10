package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.scripts.*
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.TravelToCoordinatesTask
import fr.lewon.dofus.bot.util.network.GameInfo

object TravelScript : DofusBotScript("Travel") {

    private val xParameter = DofusBotScriptParameter(
        "x", "X coordinates of destination", "0", DofusBotScriptParameterType.INTEGER
    )

    private val yParameter = DofusBotScriptParameter(
        "y", "Y coordinates of destination", "0", DofusBotScriptParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
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

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken) {
        val travelOk = TravelToCoordinatesTask(DofusCoordinates(xParameter.value.toInt(), yParameter.value.toInt()))
            .run(logItem, gameInfo, cancellationToken)
        if (!travelOk) {
            error("Failed to reach destination")
        }
    }

}