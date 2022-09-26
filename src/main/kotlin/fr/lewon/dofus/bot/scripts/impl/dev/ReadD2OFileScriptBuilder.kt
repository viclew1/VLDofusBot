package fr.lewon.dofus.bot.scripts.impl.dev

import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ReadD2OFileScriptBuilder : DofusBotScriptBuilder("Read D2O file", true) {

    private val nameParameter = DofusBotParameter(
        "Name", "D2O File name", "", DofusBotParameterType.CHOICE, D2OUtil.getModuleNames().sorted()
    )

    private val idParameter = DofusBotParameter(
        "ID", "D2O element ID (leave empty for all values)", "", DofusBotParameterType.STRING
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(nameParameter, idParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Read the D2O file associated to the passed name."
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val name = scriptValues.getParamValue(nameParameter)
        val idStr = scriptValues.getParamValue(idParameter)
        val d2oContent = D2OUtil.getObjects(name).filter { idStr.isBlank() || it["id"].toString() == idStr }
        val mapper = ObjectMapper()
        d2oContent.forEach { println(mapper.writeValueAsString(it)) }
    }

}