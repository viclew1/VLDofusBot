package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.ExecuteHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.FetchHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

object MultipleTreasureHuntScript : DofusBotScript("Multiple treasure hunts") {

    private val resumeHuntParameter = DofusBotScriptParameter(
        "resume_hunt", "Set to true if you wish to resume an ongoing hunt", "false", DofusBotScriptParameterType.BOOLEAN
    )

    private val huntCountParameter = DofusBotScriptParameter(
        "hunt_count", "Amount of hunts to process before stopping", "50", DofusBotScriptParameterType.INTEGER
    )

    private val cleanCacheParameter = DofusBotScriptParameter(
        "clean_cache_every", "Amount of hunts before cleaning Dofus cache", "12", DofusBotScriptParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            resumeHuntParameter,
            huntCountParameter,
            cleanCacheParameter
        )
    }

    private val huntFetchDurations = ArrayList<Long>()
    private val huntDurations = ArrayList<Long>()
    private val successRateStat = DofusBotScriptStat("Success rate")
    private val averageHuntFetchDurationStat = DofusBotScriptStat("Average hunt fetch duration")
    private val averageHuntDurationStat = DofusBotScriptStat("Average hunt duration")

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf(
            successRateStat,
            averageHuntFetchDurationStat,
            averageHuntDurationStat
        )
    }

    override fun getDescription(): String {
        val resumeHunt = resumeHuntParameter.value.toBoolean()
        val huntCount = huntCountParameter.value.toInt()
        val cleanCacheEvery = cleanCacheParameter.value.toInt()
        var description = "Executes $huntCount starting with the current treasure hunt by : \n"
        if (!resumeHunt) description += " - Reaching treasure hunt start location \n"
        description += " - Finding hints and resolving treasure hunt steps \n"
        description += " - Fighting the chest at the end \n"
        description += "Dofus cache will be cleaned every $cleanCacheEvery hunts"
        return description
    }

    override fun execute(logItem: LogItem?) {
        clearStats()
        var successCount = 0

        if (TreasureHuntUtil.isFightStep()) {
            TreasureHuntUtil.fight(logItem)
        }

        for (i in 0 until huntCountParameter.value.toInt()) {
            val fetchStartTimeStamp = System.currentTimeMillis()
            if (!TreasureHuntUtil.isHuntPresent()) {
                FetchHuntTask().run(logItem)
            }
            val fetchDuration = System.currentTimeMillis() - fetchStartTimeStamp
            huntFetchDurations.add(fetchDuration)
            averageHuntFetchDurationStat.value = FormatUtil.durationToStr(huntFetchDurations.average().toLong())

            val huntStartTimeStamp = System.currentTimeMillis()

            ReachMapTask(TreasureHuntUtil.getLastHintMap()).run(logItem)
            val success = ExecuteHuntTask().run(logItem)
            WaitUtil.sleep(300)

            val huntDuration = System.currentTimeMillis() - huntStartTimeStamp
            huntDurations.add(huntDuration)
            averageHuntDurationStat.value = FormatUtil.durationToStr(huntDurations.average().toLong())

            if (success) {
                VldbLogger.log("Hunt succeeded")
                successCount++
            } else {
                VldbLogger.log("Hunt failed")
            }
            successRateStat.value = "$successCount / ${i + 1}"
            if (!success) {
                WaitUtil.sleep(600 * 1000 - huntDuration)
                TreasureHuntUtil.giveUpHunt()
            }
        }
    }

    private fun clearStats() {
        huntDurations.clear()
        successRateStat.resetValue()
        averageHuntDurationStat.resetValue()
    }
}