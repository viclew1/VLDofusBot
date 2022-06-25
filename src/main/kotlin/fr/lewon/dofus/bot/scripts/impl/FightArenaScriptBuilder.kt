package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.VldbScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.arena.ProcessArenaGameTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object FightArenaScriptBuilder : DofusBotScriptBuilder("Fight in arena") {

    private val winRatio = DofusBotScriptStat("Win ratio")

    private val fightCountParameter = DofusBotParameter(
        "Fight count",
        "Amount of arena fights to do before stopping",
        "20",
        DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(fightCountParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf(winRatio)
    }

    override fun getDescription(): String {
        return "Fight in 1v1 arena"
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: VldbScriptValues) {
        val fightCount = scriptValues.getParamValue(fightCountParameter).toInt()
        for (i in 0 until fightCount) {
            if (!ProcessArenaGameTask().run(logItem, gameInfo)) {
                error("Failed to process an arena game")
            }
            winRatio.value = "TODO / ${i + 1}"
        }
    }

}