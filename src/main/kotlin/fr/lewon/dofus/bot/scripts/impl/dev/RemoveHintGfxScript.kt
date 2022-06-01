package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.impl.HintManager
import fr.lewon.dofus.bot.util.network.info.GameInfo

class RemoveHintGfxScript : DofusBotScript("Remove hint GFX ID", true) {

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

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(currentHintParameter, hintNameParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Removes an hint label to GFX ID match in case you made a mistake or a match changed"
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
        HintManager.removeHintGfxMatch(hintLabel)
        gameInfo.logger.addSubLog("GFX ID removed for hint : $hintLabel", logItem)
    }

}