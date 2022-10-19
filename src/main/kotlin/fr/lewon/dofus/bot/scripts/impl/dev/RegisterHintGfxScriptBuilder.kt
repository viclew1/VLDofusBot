package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import fr.lewon.dofus.bot.util.network.info.GameInfo

object RegisterHintGfxScriptBuilder : DofusBotScriptBuilder("Register hint GFX ID", true) {

    private val currentHintParameter = DofusBotParameter(
        "Current hint",
        "If checked, ignores hint name parameter and associates the GFX ID to the current hint",
        "false",
        DofusBotParameterType.BOOLEAN
    )

    private val hintNameParameter = DofusBotParameter(
        "Hint label",
        "Hint label",
        "",
        DofusBotParameterType.STRING,
        displayCondition = { it.getParamValue(currentHintParameter) == "false" }
    )

    private val gfxIdParameter = DofusBotParameter(
        "GFX ID", "GFX element ID", "0", DofusBotParameterType.INTEGER
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(currentHintParameter, hintNameParameter, gfxIdParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Register a GFX ID matching an hint label"
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val currentHintValue = scriptValues.getParamValue(currentHintParameter).toBoolean()
        val hintLabel = if (currentHintValue) {
            val currentHint = gameInfo.treasureHunt?.knownStepsList?.lastOrNull()
                ?: error("No current hint found")
            if (currentHint !is TreasureHuntStepFollowDirectionToPOI) {
                error("Current hint isn't a point of interest, impossible to register it")
            }
            PointOfInterestManager.getPointOfInterest(currentHint.poiLabelId)?.label
                ?: error("No POI for id : ${currentHint.poiLabelId}")
        } else {
            scriptValues.getParamValue(hintNameParameter).takeIf { it.isNotBlank() }
                ?: error("Hint name cannot be empty")
        }
        val gfxIdStr = scriptValues.getParamValue(gfxIdParameter)
        val gfxId = gfxIdStr.toIntOrNull() ?: error("Invalid GFX ID : $gfxIdStr")
        TreasureHintManager.addHintGfxMatch(hintLabel, gfxId)
        gameInfo.logger.addSubLog("GFX ID $gfxId associated to hint : $hintLabel", logItem)
    }

}