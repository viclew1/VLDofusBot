package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.model.dungeon.Dungeon
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.ChoiceParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightDungeonTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object FightDungeonScriptBuilder : DofusBotScriptBuilder("Fight dungeon") {

    private val DUNGEON_BY_NAME = Dungeon.entries.associateBy { it.name }

    private val countStat = DofusBotScriptStat("Count", "0")

    private val dungeonParameter = ChoiceParameter(
        "Dungeon",
        "The dungeon you want to farm",
        Dungeon.entries.first(),
        getAvailableValues = { Dungeon.entries },
        itemValueToString = { it.name },
        stringToItemValue = { DUNGEON_BY_NAME[it] ?: error("Dungeon not found : $it") }
    )

    override fun getParameters(): List<DofusBotParameter<*>> {
        return listOf(dungeonParameter)
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf(countStat)
    }

    override fun getDescription(): String {
        return "Runs a dungeon until you run out of keys"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val dungeon = parameterValues.getParamValue(dungeonParameter)
        var count = 0
        while (FightDungeonTask(dungeon).run(logItem, gameInfo)) {
            count++
            statValues[countStat] = count.toString()
        }
    }

}