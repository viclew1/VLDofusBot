package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

object ResumeHuntScript : DofusBotScript("Resume hunt") {

    private var executingChainHunts: Boolean = false

    override fun getParameters(): List<DofusBotScriptParameter> {
        return emptyList()
    }

    override fun getStats(): List<Pair<String, String>> {
        if (!executingChainHunts) {
            return emptyList()
        }
        return ChainHuntsScript.getStats()
    }

    override fun getDescription(): String {
        return "Resumes ongoing hunt and proceeds to run [${ChainHuntsScript.name}] script after succeeding"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        executingChainHunts = false
        executeHunt()
        clickChain(listOf("fight/fight.png"), "fight/ready.png")
        runScript(FightScript)
        executingChainHunts = true
        runScript(ChainHuntsScript)
    }

}