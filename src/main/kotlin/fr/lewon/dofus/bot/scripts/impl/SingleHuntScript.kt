package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

object SingleHuntScript : DofusBotScript("Single hunt") {

    private val resumeHuntParameter = DofusBotScriptParameter(
        "Resume hunt",
        "Set to true if you're resuming an onging hunt",
        "false",
        DofusBotScriptParameterType.BOOLEAN
    )

    private val fightParameter = DofusBotScriptParameter(
        "Fight",
        "Set to true to fight the chest at the end of the hunt",
        "false",
        DofusBotScriptParameterType.BOOLEAN
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            resumeHuntParameter,
            fightParameter
        )
    }

    override fun getStats(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Executes a single hunt, you might want to do this when reaching a portal to a dimension or an epic hunt"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val resumeHunt = resumeHuntParameter.value.toBoolean()
        val fight = fightParameter.value.toBoolean()

        if (!resumeHunt) {
            runScript(FetchAHuntScript)
            runScript(ReachHuntStartScript)
        }
        executeHunt()
        if (fight) {
            clickChain(listOf("fight/fight.png"), "fight/ready.png")
            runScript(FightScript)
        }
    }

}