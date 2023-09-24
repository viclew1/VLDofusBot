package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.harvest.TransferItemsToBankTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

object TestScriptBuilder : DofusBotScriptBuilder("Test", true) {

    override fun getParameters(): List<DofusBotParameter<*>> {
        return listOf()
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test script for development only"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        //FightTask().run(logItem, gameInfo)
        TransferItemsToBankTask().run(logItem, gameInfo)
    }

}