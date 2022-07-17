package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import fr.lewon.dofus.bot.util.network.info.GameInfo

class RegisterHintGfxScript : DofusBotScript("Register hint GFX ID", true) {

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
        parentParameter = currentHintParameter,
        displayCondition = { currentHintParameter.value == "false" }
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

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val currentHintValue = currentHintParameter.value.toBoolean()
        val hintLabel = if (currentHintValue) {
            val currentHint = gameInfo.treasureHunt?.huntSteps?.lastOrNull()
                ?: error("No current hint found")
            if (currentHint !is TreasureHuntStepFollowDirectionToPOI) {
                error("Current hint isn't a point of interest, impossible to register it")
            }
            PointOfInterestManager.getPointOfInterest(currentHint.poiLabelId)?.label
                ?: error("No POI for id : ${currentHint.poiLabelId}")
        } else hintNameParameter.value.takeIf { it.isNotBlank() } ?: error("Hint name cannot be empty")
        val gfxId = gfxIdParameter.value.toIntOrNull()
            ?: error("Invalid GFX ID : ${gfxIdParameter.value}")
        TreasureHintManager.addHintGfxMatch(hintLabel, gfxId)
        gameInfo.logger.addSubLog("GFX ID $gfxId associated to hint : $hintLabel", logItem)
    }

}