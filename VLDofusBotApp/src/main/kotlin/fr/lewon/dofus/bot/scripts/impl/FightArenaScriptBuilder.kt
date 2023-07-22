package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.arena.ProcessArenaGameTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object FightArenaScriptBuilder : DofusBotScriptBuilder("Fight in arena") {

    private val winRatioStat = DofusBotScriptStat("Win ratio")

    private val fightCountParameter = IntParameter(
        "Fight count",
        "Amount of arena fights to do before stopping",
        20,
    )

    override fun getParameters(): List<DofusBotParameter<*>> {
        return listOf(fightCountParameter)
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf(winRatioStat)
    }

    override fun getDescription(): String {
        return "Fight in 1v1 arena"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val fightCount = parameterValues.getParamValue(fightCountParameter)
        for (i in 0 until fightCount) {
            if (!ProcessArenaGameTask().run(logItem, gameInfo)) {
                error("Failed to process an arena game")
            }
            statValues[winRatioStat] = "TODO / ${i + 1}"
        }
    }

}