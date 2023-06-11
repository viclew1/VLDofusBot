package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class HarvestAllResourcesTask(private val itemIdsToHarvest: List<Double>) : BooleanDofusBotTask() {

    private var totalHarvestableCount: Int? = null
    private var currentHarvestedCount: Int = 0

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val toIgnoreResources = ArrayList<InteractiveElement>()
        val cellDataByHarvestableInteractiveElements = gameInfo.interactiveElements.filter { it.onCurrentMap }
            .associateWith { InteractiveUtil.getElementCellData(gameInfo, it) }
        totalHarvestableCount = cellDataByHarvestableInteractiveElements.size
        currentHarvestedCount = 0
        while (true) {
            gameInfo.logger.closeLog("[$currentHarvestedCount/$totalHarvestableCount]", logItem)
            if (gameInfo.inventoryWeight >= 0 && gameInfo.weightMax >= 0 && gameInfo.inventoryWeight + 50 > gameInfo.weightMax) {
                error("Inventory is full")
            }
            val interactiveElement = getNextElementToHarvest(
                gameInfo = gameInfo,
                cellDataByHarvestableInteractiveElements = cellDataByHarvestableInteractiveElements,
                toIgnoreResources = toIgnoreResources
            ) ?: return true
            HarvestResourceTask(interactiveElement).run(logItem, gameInfo)
            toIgnoreResources.add(interactiveElement)
            currentHarvestedCount++
        }
    }

    private fun getNextElementToHarvest(
        gameInfo: GameInfo,
        cellDataByHarvestableInteractiveElements: Map<InteractiveElement, CompleteCellData>,
        toIgnoreResources: List<InteractiveElement>
    ): InteractiveElement? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player")
        val invalidMoveCells = MoveUtil.getInvalidCells(gameInfo).map { it.cellId }
        return cellDataByHarvestableInteractiveElements.filter {
            !toIgnoreResources.contains(it.key)
                    && !invalidMoveCells.contains(it.value.cellId)
                    && shouldHarvest(it.key)
        }.minByOrNull {
            val cell = gameInfo.dofusBoard.getCell(it.value.cellId)
            cell.neighbors.plus(cell).mapNotNull { c ->
                gameInfo.dofusBoard.getPathLength(c.cellId, playerCellId)
            }.minOrNull() ?: Int.MAX_VALUE
        }?.key
    }

    private fun shouldHarvest(interactiveElement: InteractiveElement): Boolean = interactiveElement.enabledSkills.any {
        itemIdsToHarvest.contains(SkillManager.getSkill(it.skillId.toDouble())?.gatheredResourceItem?.id)
    }

    override fun onStarted(): String {
        return "Harvesting resources ..."
    }

}