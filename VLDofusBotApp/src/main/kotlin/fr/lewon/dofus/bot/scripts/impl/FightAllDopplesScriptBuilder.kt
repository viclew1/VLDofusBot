package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.entity.NpcManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.d2o.managers.quest.QuestManager
import fr.lewon.dofus.bot.core.d2o.managers.quest.QuestObjectiveManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.scripts.tasks.impl.init.UpdateQuestsTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object FightAllDopplesScriptBuilder : DofusBotScriptBuilder("Fight all dopples") {

    private const val DOPPLE_GLOBAL_QUEST_ID = 470

    private val countStat = DofusBotScriptStat("Count")

    override fun getParameters(): List<DofusBotParameter> = emptyList()

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Fight all dopples"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        scriptValues: ScriptValues,
        statValues: HashMap<DofusBotScriptStat, String>
    ) {
        UpdateQuestsTask().run(logItem, gameInfo)
        val objectiveIds = if (!gameInfo.activeQuestIds.contains(DOPPLE_GLOBAL_QUEST_ID)) {
            getNewQuestObjectives(gameInfo, logItem)
        } else {
            getCurrentQuestObjectives(gameInfo)
        }

        val remainingObjectives = objectiveIds.map { QuestObjectiveManager.getQuestObjective(it) }
            .toMutableList()
        val toFightCount = remainingObjectives.size
        statValues[countStat] = "0 / $toFightCount"
        var foughtCount = 0
        while (remainingObjectives.isNotEmpty()) {
            val destMapIds = remainingObjectives.map { it.mapId }
            doFightDopple(gameInfo, logItem, destMapIds, listOf(-1, -1))
            val objective = remainingObjectives.firstOrNull { it.mapId == gameInfo.currentMap.id }
                ?: error("Unexpected map ID, fight might have been lost")
            statValues[countStat] = "${++foughtCount} / $toFightCount"
            remainingObjectives.remove(objective)
        }
    }

    private fun getNewQuestObjectives(gameInfo: GameInfo, logItem: LogItem): List<Int> {
        val subLogItem = gameInfo.logger.addSubLog("Fetching dopples global quest at Sram temple", logItem)
        doFightDopple(gameInfo, subLogItem, listOf(183768066.0), listOf(-1, 12457, -1))
        return QuestManager.getQuest(DOPPLE_GLOBAL_QUEST_ID).steps.flatMap { it.objectiveIds }.minus(3199)
    }

    private fun getCurrentQuestObjectives(gameInfo: GameInfo): List<Int> {
        val quest = QuestManager.getQuest(DOPPLE_GLOBAL_QUEST_ID)
        val questObjectiveIds = quest.steps.flatMap { it.objectiveIds }
        return gameInfo.activeObjectiveIds.filter { objectiveId ->
            objectiveId in questObjectiveIds
        }
    }

    private fun doFightDopple(gameInfo: GameInfo, logItem: LogItem, mapIds: List<Double>, npcOptionIds: List<Int>) {
        val maps = mapIds.map { mapId -> MapManager.getDofusMap(mapId) }
        if (!ReachMapTask(maps).run(logItem, gameInfo)) {
            error("Couldn't reach next temple")
        }
        if (!NpcSpeakTask(getDoppleNpcId(gameInfo), npcOptionIds).run(logItem, gameInfo)) {
            error("Couldn't talk to dopple NPC")
        }
        if (!FightTask().run(logItem, gameInfo)) {
            error("Couldn't fight dopple")
        }
    }

    private fun getDoppleNpcId(gameInfo: GameInfo) = gameInfo.entityIdByNpcId.keys.firstOrNull {
        NpcManager.getNPC(it.toDouble()).name.endsWith("Peule")
    } ?: error("No dopple NPC found")

}