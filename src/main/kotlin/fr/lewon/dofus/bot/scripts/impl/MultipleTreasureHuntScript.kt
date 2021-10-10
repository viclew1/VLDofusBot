package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.ExecuteHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.FetchHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem
import fr.lewon.dofus.bot.util.logs.VldbLogger

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

    private val huntDurations = ArrayList<Long>()
    private var successRateStat = Pair("Success rate", "")
    private var averageHuntDurationStat = Pair("Average hunt duration", "")

    override fun getStats(): List<Pair<String, String>> {
        return listOf(
            successRateStat,
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
            if (!TreasureHuntUtil.isHuntPresent()) {
                FetchHuntTask().run(logItem)
            }

            val startTimeStamp = System.currentTimeMillis()

            var success = true
            while (TreasureHuntUtil.isHuntPresent()) {
                TreasureHuntUtil.refreshHuntIfNeeded(logItem)
                ReachMapTask(TreasureHuntUtil.getLastHintMap()).run(logItem)
                success = ExecuteHuntTask().run(logItem)
                WaitUtil.sleep(300)
            }

            val duration = System.currentTimeMillis() - startTimeStamp
            updateStats(duration, successCount, i + 1)

            if (success) {
                VldbLogger.log("Hunt succeeded")
                successCount++
            } else {
                VldbLogger.log("Hunt failed")
                WaitUtil.sleep(600 * 1000 - duration)
                TreasureHuntUtil.giveUpHunt()
            }
        }
    }

    private fun updateStats(duration: Long, successCount: Int, huntsCount: Int) {
        huntDurations.add(duration)
        successRateStat = Pair(successRateStat.first, "$successCount / $huntsCount")
        averageHuntDurationStat = Pair(averageHuntDurationStat.first, getHuntDurationsStr())
    }

    private fun getHuntDurationsStr(): String {
        val average = huntDurations.average().toLong()
        val minutes = average / (60 * 1000)
        val seconds = ((average - minutes * 60 * 1000) / 1000).toString().padStart(2, '0')
        return "${minutes}M ${seconds}S"
    }

    private fun clearStats() {
        huntDurations.clear()
        successRateStat = Pair(successRateStat.first, "")
        averageHuntDurationStat = Pair(averageHuntDurationStat.first, "")
    }
}