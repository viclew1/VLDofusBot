package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

object ChainHuntsScript : DofusBotScript("Chain hunts") {

    val cleanCachePeriodParameter = DofusBotScriptParameter(
        "Clean cache every ...",
        "The game cache will be cleaned every X hunts (succeeded or not). Enter any number <= 0 to never clean the cache",
        "25",
        DofusBotScriptParameterType.INTEGER
    )

    val continueOnFailParameter = DofusBotScriptParameter(
        "Continue on fail",
        "If true, cancels failed hunt and fetches another one. Else, script stops on hunt fail",
        "true",
        DofusBotScriptParameterType.BOOLEAN
    )

    val huntsAmountParameter = DofusBotScriptParameter(
        "Hunts amount",
        "Amount of hunt to process before stopping",
        "5",
        DofusBotScriptParameterType.INTEGER
    )

    private var huntsDone: Int = 0
    private var huntsSuccess: Int = 0
    private var huntDurations: ArrayList<Long> = ArrayList()
    private var huntAverageDurationDisplay: String = "/"

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            continueOnFailParameter,
            huntsAmountParameter,
            cleanCachePeriodParameter
        )
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf(
            Pair("Hunt successes", "$huntsSuccess/$huntsDone"),
            Pair("Average hunt time", huntAverageDurationDisplay)
        )
    }

    override fun getDescription(): String {
        return "Fetches a new hunt, reaches the hunt start, executes the hunt and fights the chest. And does it ${huntsAmountParameter.value} times."
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val continueOnFail = continueOnFailParameter.value.toBoolean()
        val huntsAmount = huntsAmountParameter.value.toInt()
        val cleanCachePeriod = cleanCachePeriodParameter.value.toInt()

        huntsDone = 0
        huntsSuccess = 0
        huntDurations = ArrayList()
        huntAverageDurationDisplay = "/"

        var cpt = 0
        for (i in 0 until huntsAmount) {
            runScript(FetchAHuntScript)
            val start = getTime()
            try {
                runScript(ReachHuntStartScript)
                executeHunt()
                clickChain(listOf("fight/fight.png"), "fight/ready.png")
                runScript(FightScript)
                huntsDone++
                huntsSuccess++
                huntDurations.add(getTime() - start)
                val average = huntDurations.average().toLong()
                val minutes = average / (60 * 1000)
                val seconds =
                    ((average - minutes * 60 * 1000) / 1000).toString().padStart(2, '0')
                huntAverageDurationDisplay = "${minutes}M ${seconds}S"

            } catch (e: Exception) {
                huntsDone++
                if (!continueOnFail) {
                    throw e
                }
                val duration = getTime() - start
                sleep(610000 - duration)
                if (getHuntPanel() != null) {
                    clickChain(listOf("cancel_hunt.png", "ok_cancel.png"))
                }
            } finally {
                clearCache()
                if (++cpt == cleanCachePeriod) {
                    cpt = 0
                    runScript(CleanCacheScript)
                }
            }
        }
    }

}