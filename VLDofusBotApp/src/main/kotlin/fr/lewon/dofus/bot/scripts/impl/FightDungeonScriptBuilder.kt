package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.map.HintManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.model.dungeon.Dungeon
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightDungeonTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object FightDungeonScriptBuilder : DofusBotScriptBuilder("Fight dungeon") {

    private val DUNGEON_HINTS = HintManager.getHints(HintManager.HintType.DUNGEON)
    private val DUNGEON_BY_NAME = Dungeon.values().associateBy { it.name }

    private val countStat = DofusBotScriptStat("Count", "0")

    private val dungeonParameter = DofusBotParameter(
        "Dungeon",
        "The dungeon you want to farm",
        DUNGEON_BY_NAME.keys.first(),
        DofusBotParameterType.CHOICE,
        DUNGEON_BY_NAME.keys.toList()
    )

    override fun getParameters(): List<DofusBotParameter> {
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
        scriptValues: ScriptValues,
        statValues: HashMap<DofusBotScriptStat, String>
    ) {
        val dungeonName = scriptValues.getParamValue(dungeonParameter)
        val dungeon = DUNGEON_BY_NAME[dungeonName] ?: error("Invalid dungeon : $dungeonName")
        var count = 0
        while (FightDungeonTask(dungeon).run(logItem, gameInfo)) {
            count++
            statValues[countStat] = count.toString()
        }
    }

}