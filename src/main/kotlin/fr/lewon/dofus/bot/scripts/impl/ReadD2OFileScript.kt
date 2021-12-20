package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.d2o.D2OUtil
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.util.network.GameInfo

class ReadD2OFileScript : DofusBotScript("Read D2O file") {

    private val nameParameter = DofusBotScriptParameter(
        "name", "D2O File name", "", DofusBotScriptParameterType.STRING
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(nameParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Read the D2O file associated to the passed name."
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val name = nameParameter.value
        val d2oContent = D2OUtil.getObjects(name)
        val subLogItem = gameInfo.logger.addSubLog("D20 File [$name] content : ", logItem, d2oContent.size)
        d2oContent.forEach { gameInfo.logger.addSubLog(it.toString(), subLogItem) }
        subLogItem.closeLog("End")
    }

}