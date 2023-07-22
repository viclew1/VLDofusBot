package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.BooleanParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.OpenZaapInterfaceTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

object ExploreAllZaapsScriptBuilder : DofusBotScriptBuilder("Explore all zaaps") {

    private val exploredStat = DofusBotScriptStat("Explored")

    private val useZaapsParameter = BooleanParameter(
        "Use zaaps",
        "Check if you want to allow zaaps for the travel",
        true,
        parametersGroup = 2
    )

    override fun getParameters(): List<DofusBotParameter<*>> {
        return listOf(useZaapsParameter)
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return listOf(exploredStat)
    }

    override fun getDescription(): String {
        return "Explores all accessible zaap maps"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        val useZaaps = parameterValues.getParamValue(useZaapsParameter)
        val allZaapMaps = TravelUtil.getAllZaapMaps()
        val registeredZaaps = OpenZaapInterfaceTask().run(logItem, gameInfo).toMutableList()
        val nonExploredZaaps = allZaapMaps.minus(registeredZaaps.toSet()).toMutableList()
        val closeZaapInterfaceButtonBounds = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "btn_close")
        MouseUtil.leftClick(gameInfo, closeZaapInterfaceButtonBounds.getCenter())
        LeaveHavenBagTask().run(logItem, gameInfo)
        val toExploreZaaps = nonExploredZaaps.filter { zaapMap ->
            zaapMap.subArea.area.superAreaId == gameInfo.currentMap.subArea.area.superAreaId
        }.toMutableList()
        val totalSize = toExploreZaaps.size
        statValues[exploredStat] = "0 / $totalSize"
        while (toExploreZaaps.isNotEmpty()) {
            val subLogItem = gameInfo.logger.addSubLog("Reaching next Zaap ...", logItem)
            val toUseZaaps = if (useZaaps) registeredZaaps else emptyList()
            if (!ReachMapTask(toExploreZaaps, toUseZaaps).run(subLogItem, gameInfo)) {
                gameInfo.logger.closeLog("KO", subLogItem)
                error("Couldn't explore all zaaps")
            }
            gameInfo.logger.closeLog("OK", subLogItem)
            toExploreZaaps.remove(gameInfo.currentMap)
            registeredZaaps.add(gameInfo.currentMap)
            statValues[exploredStat] = "${totalSize - toExploreZaaps.size} / $totalSize"
        }
    }

}