package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.network.info.GameInfo

object ReadSpellInfoScriptBuilder : DofusBotScriptBuilder("Read spell info", true) {

    private val nameParameter = DofusBotParameter(
        "Name", "Spell name", "", DofusBotParameterType.STRING
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(nameParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Read the spell info."
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val name = scriptValues.getParamValue(nameParameter)
        if (name.isEmpty()) {
            error("Missing name parameter")
        }
        val spells = D2OUtil.getObjects("Spells")
            .filter { name.lowercase() == I18NUtil.getLabel(it["nameId"].toString().toInt())?.lowercase() }
        val spellLevelIds = spells.flatMap { it["spellLevels"] as List<Int> }
        val spellLevels = D2OUtil.getObjects("SpellLevels")
            .filter { spellLevelIds.contains(it["id"].toString().toInt()) }
        val spellLevel = spellLevels.maxByOrNull { it["minPlayerLevel"].toString().toInt() }
            ?: error("Couldn't find a spell level")
        gameInfo.logger.addSubLog(spellLevel.toString(), logItem)
    }

}