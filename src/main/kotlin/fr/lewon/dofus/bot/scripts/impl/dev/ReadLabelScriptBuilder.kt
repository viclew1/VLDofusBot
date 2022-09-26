package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ReadLabelScriptBuilder : DofusBotScriptBuilder("Read label", true) {

    private val nameIdParameter = DofusBotParameter(
        "Name ID", "Label name ID", "0", DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(nameIdParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Read the label by its ID."
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val nameId = scriptValues.getParamValue(nameIdParameter)
        if (nameId.isEmpty()) {
            error("Missing name ID parameter")
        }
        val label = I18NUtil.getLabel(nameId.toInt()) ?: error("Couldn't find label")
        gameInfo.logger.addSubLog(label, logItem)
    }

}