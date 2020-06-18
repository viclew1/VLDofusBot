package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter

object ChainHuntsScript : DofusBotScript("Chain hunts") {

    val continueOnFailParameter = DofusBotScriptParameter(
        "Continue on fail",
        "If value is [yes], cancels failed hunt and fetches another one. Else, script stops on hunt fail",
        "yes"
    )

    val huntsAmountParameter = DofusBotScriptParameter(
        "Hunts amount",
        "Amount of hunt to process before stopping",
        "5"
    )

    private var huntsDone: Int = 0
    private var huntsSuccess: Int = 0
    private var huntDurations: ArrayList<Long> = ArrayList()
    private var huntAverageDurationDisplay: String = "/"

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            continueOnFailParameter,
            huntsAmountParameter
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

    override fun doExecute(parameters: Map<String, DofusBotScriptParameter>) {
        val continueOnFail = "yes".toLowerCase() == continueOnFailParameter.value.toLowerCase()
        val huntsAmount = huntsAmountParameter.value.toInt()

        huntsDone = 0
        huntsSuccess = 0
        huntDurations = ArrayList()
        huntAverageDurationDisplay = "/"

        for (i in 0 until huntsAmount) {
            runScript(FetchAHuntScript)
            val start = getTime()
            try {
                runScript(ReachHuntStartScript)
                executeHunt()
                huntsDone++
                runScript(FightScript)
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
            }
        }
    }

}