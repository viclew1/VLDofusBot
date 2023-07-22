package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.gui.util.SoundType
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.model.hunt.HuntLevel
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.BooleanParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.ChoiceParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.ExecuteHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.FetchHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.RefreshHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ExecuteTreasureHuntScriptBuilder : DofusBotScriptBuilder("Execute treasure hunt") {

    private val huntLevelParameter = ChoiceParameter(
        "Hunt level",
        "Hunt level",
        HuntLevel.LVL200,
        getAvailableValues = { HuntLevel.values().toList() },
        itemValueToString = { it.label },
        stringToItemValue = { HuntLevel.fromLabel(it) ?: error("Invalid hunt level") }
    )

    private val huntCountParameter = IntParameter(
        "Hunt count", "Amount of hunts to process before stopping", 50
    )

    private val continueOnFailureParameter = BooleanParameter(
        "Continue on failure",
        "If true, the bot gives up a hunt when it fails to find a hint and fetches a new one",
        true,
    )

    override fun getParameters(): List<DofusBotParameter<*>> {
        return listOf(
            huntLevelParameter,
            huntCountParameter,
            continueOnFailureParameter
        )
    }

    private val successRateStat = DofusBotScriptStat("Success rate")
    private val averageHuntFetchDurationStat = DofusBotScriptStat("Average hunt fetch duration")
    private val averageHuntDurationStat = DofusBotScriptStat("Average hunt duration")

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf(
            successRateStat,
            averageHuntFetchDurationStat,
            averageHuntDurationStat,
        )
    }

    override fun getDescription(): String {
        var description = "Executes hunt(s) starting with the current treasure hunt by : \n"
        description += " - Reaching treasure hunt start location \n"
        description += " - Finding hints and resolving treasure hunt steps \n"
        description += " - Fighting the chest at the end"
        return description
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        if (gameInfo.treasureHunt == null && TreasureHuntUtil.isHuntPresent(gameInfo)) {
            if (!RefreshHuntTask().run(logItem, gameInfo)) {
                error("Couldn't refresh hunt")
            }
        }
        var successCount = 0
        val huntLevel = parameterValues.getParamValue(huntLevelParameter)
        val continueOnFailure = parameterValues.getParamValue(continueOnFailureParameter)
        val huntFetchDurations = ArrayList<Long>()
        val huntDurations = ArrayList<Long>()

        for (i in 0 until parameterValues.getParamValue(huntCountParameter)) {
            val fetchStartTimeStamp = System.currentTimeMillis()
            if (gameInfo.treasureHunt == null) {
                if (FetchHuntTask(huntLevel).run(logItem, gameInfo)) {
                    huntFetchDurations.add(System.currentTimeMillis() - fetchStartTimeStamp)
                    statValues[averageHuntFetchDurationStat] =
                        FormatUtil.durationToStr(huntFetchDurations.average().toLong())
                } else {
                    error("Couldn't fetch a new hunt")
                }
            }

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
                statValues[averageHuntDurationStat] = FormatUtil.durationToStr(huntDurations.average().toLong())
            }
            statValues[successRateStat] = "$successCount / ${i + 1}"
            if (!success) {
                if (continueOnFailure) {
                    SoundType.FAILED.playSound()
                    val timeUntilCancel = 2 * 60 * 1000 - huntDuration
                    gameInfo.logger.addSubLog("Hunt failed, will give it up and restart (2 minutes wait)", logItem)
                    if (timeUntilCancel > 0) {
                        WaitUtil.sleep(timeUntilCancel)
                    }
                    if (gameInfo.treasureHunt != null) {
                        TreasureHuntUtil.giveUpHunt(gameInfo)
                    }
                } else {
                    error("Hunt failed")
                }
            }
        }
    }

}