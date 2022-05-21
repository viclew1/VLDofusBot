package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.alert.SoundType
import fr.lewon.dofus.bot.model.hunt.HuntLevel
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.ExecuteHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.FetchHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecuteTreasureHuntScript : DofusBotScript("Execute treasure hunt") {

    private val huntLevelParameter = DofusBotParameter(
        "Hunt level", "Hunt level", "200", DofusBotParameterType.CHOICE, HuntLevel.values().map { it.label }
    )

    private val huntCountParameter = DofusBotParameter(
        "Hunt count", "Amount of hunts to process before stopping", "50", DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(
            huntLevelParameter,
            huntCountParameter,
        )
    }

    private val huntFetchDurations = ArrayList<Long>()
    private val huntDurations = ArrayList<Long>()
    private val successRateStat = DofusBotScriptStat("Success rate")
    private val averageHuntFetchDurationStat = DofusBotScriptStat("Average hunt fetch duration")
    private val averageHuntDurationStat = DofusBotScriptStat("Average hunt duration")
    private val nextRestartInStat = DofusBotScriptStat("Next restart in")

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf(
            successRateStat,
            averageHuntFetchDurationStat,
            averageHuntDurationStat,
            nextRestartInStat
        )
    }

    override fun getDescription(): String {
        val huntCount = huntCountParameter.value.toInt()
        var description = "Executes $huntCount hunt(s) starting with the current treasure hunt by : \n"
        description += " - Reaching treasure hunt start location \n"
        description += " - Finding hints and resolving treasure hunt steps \n"
        description += " - Fighting the chest at the end"
        return description
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        var successCount = 0
        val huntLevel = HuntLevel.fromLabel(huntLevelParameter.value)
            ?: error("Invalid hunt level")

        for (i in 0 until huntCountParameter.value.toInt()) {
            val fetchStartTimeStamp = System.currentTimeMillis()
            if (gameInfo.treasureHunt == null && !FetchHuntTask(huntLevel).run(logItem, gameInfo)) {
                error("Couldn't fetch a new hunt")
            }
            val fetchDuration = System.currentTimeMillis() - fetchStartTimeStamp
            huntFetchDurations.add(fetchDuration)
            averageHuntFetchDurationStat.value = FormatUtil.durationToStr(huntFetchDurations.average().toLong())

            val huntStartTimeStamp = System.currentTimeMillis()

            if (!ReachMapTask(listOf(TreasureHuntUtil.getLastHintMap(gameInfo))).run(logItem, gameInfo)) {
                error("Couldn't reach hunt start")
            }
            val success = ExecuteHuntTask().run(logItem, gameInfo)
            WaitUtil.sleep(300)

            val huntDuration = System.currentTimeMillis() - huntStartTimeStamp

            if (success) {
                successCount++
                huntDurations.add(huntDuration)
                averageHuntDurationStat.value = FormatUtil.durationToStr(huntDurations.average().toLong())
            }
            successRateStat.value = "$successCount / ${i + 1}"
            if (!success) {
                SoundType.FAILED.playSound()
                WaitUtil.sleep(600 * 1000 - huntDuration)
                if (gameInfo.treasureHunt != null) {
                    TreasureHuntUtil.giveUpHunt(gameInfo)
                }
            }
        }
    }

}