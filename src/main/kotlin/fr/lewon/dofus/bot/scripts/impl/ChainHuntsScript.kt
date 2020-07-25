package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

object ChainHuntsScript : DofusBotScript("Chain hunts") {

    private val cleanCachePeriodParameter = DofusBotScriptParameter(
        "Clean cache every ...",
        "The game cache will be cleaned every X hunts (succeeded or not). Enter any number <= 0 to never clean the cache",
        "12",
        DofusBotScriptParameterType.INTEGER
    )

    private val continueOnFailParameter = DofusBotScriptParameter(
        "Continue on fail",
        "If true, cancels failed hunt and fetches another one. Else, script stops on hunt fail",
        "true",
        DofusBotScriptParameterType.BOOLEAN
    )

    private val huntsAmountParameter = DofusBotScriptParameter(
        "Hunts amount",
        "Amount of hunt success before stopping",
        "50",
        DofusBotScriptParameterType.INTEGER
    )

    private val resumeParameter = DofusBotScriptParameter(
        "Resume hunt",
        "Set to true if there is an ongoing hunt step, to avoid reaching the hunt start first",
        "false",
        DofusBotScriptParameterType.BOOLEAN
    )

    private var huntsAmount: Int = 0
    private var huntsSuccess: Int = 0
    private var huntsFails: Int = 0
    private var huntsUntilCacheClean: Int = 0
    private var huntDurations: ArrayList<Long> = ArrayList()
    private var huntAverageDurationDisplay: String = "/"

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            continueOnFailParameter,
            huntsAmountParameter,
            cleanCachePeriodParameter,
            resumeParameter
        )
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf(
            Pair("Remaining hunts", "${huntsAmount - huntsSuccess}"),
            Pair("Hunt successes", "$huntsSuccess"),
            Pair("Hunt fails", "$huntsFails"),
            Pair("Average hunt time", huntAverageDurationDisplay),
            Pair("Hunts until cache clean", "$huntsUntilCacheClean")
        )
    }

    override fun getDescription(): String {
        return "Fetches a new hunt, reaches the hunt start, executes the hunt and fights the chest. And does it ${huntsAmountParameter.value} times. Cleans the game cache every ${cleanCachePeriodParameter.value} hunts."
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val continueOnFail = continueOnFailParameter.value.toBoolean()
        huntsAmount = huntsAmountParameter.value.toInt()
        val cleanCachePeriod = cleanCachePeriodParameter.value.toInt()
        val resume = resumeParameter.value.toBoolean()
        huntsUntilCacheClean = cleanCachePeriod

        huntsFails = 0
        huntsSuccess = 0
        huntDurations = ArrayList()
        huntAverageDurationDisplay = "/"

        if (resume) {
            executeHunt()
            clickChain(listOf("fight/fight.png"), "fight/ready.png")
            runScript(FightScript)
        }

        for (i in 0 until huntsAmount) {
            runScript(FetchAHuntScript)
            val start = getTime()
            try {
                runScript(ReachHuntStartScript)
                executeHunt()
                clickChain(listOf("fight/fight.png"), "fight/ready.png")
                runScript(FightScript)
                huntsSuccess++
                huntDurations.add(getTime() - start)
                val average = huntDurations.average().toLong()
                val minutes = average / (60 * 1000)
                val seconds =
                    ((average - minutes * 60 * 1000) / 1000).toString().padStart(2, '0')
                huntAverageDurationDisplay = "${minutes}M ${seconds}S"

            } catch (e: Exception) {
                huntsFails++
                if (!continueOnFail) {
                    throw e
                }
                val duration = getTime() - start
                sleep(610000 - duration)
                if (getHuntPanel() != null) {
                    execTimeoutOpe(
                        { clickChain(listOf("cancel_hunt.png", "ok_cancel.png")) },
                        { !imgFound("../templates/hunt_frame_top.png") })
                }
            } finally {
                huntsUntilCacheClean--
                clearMatCache()
                if (huntsUntilCacheClean == 0) {
                    runScript(RestartGameScript)
                    huntsUntilCacheClean = cleanCachePeriod
                }
            }
        }
    }

}