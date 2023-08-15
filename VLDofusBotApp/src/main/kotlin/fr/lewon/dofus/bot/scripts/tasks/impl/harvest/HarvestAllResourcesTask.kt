package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.d2p.maps.cell.CompleteCellData
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.game.interactive.InteractiveElement
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.toPointAbsolute
import fr.lewon.dofus.bot.util.network.info.GameInfo

class HarvestAllResourcesTask : BooleanDofusBotTask() {

    private var totalHarvestableCount: Int? = null
    private var currentHarvestedCount: Int = 0

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val itemIdsToHarvest = HarvestableSetsManager.getItemsToHarvest(gameInfo.character.parameters.harvestableSet)
        val toIgnoreResources = ArrayList<InteractiveElement>()
        val cellDataByHarvestableInteractiveElements = gameInfo.interactiveElements.filter { it.onCurrentMap }
            .associateWith { InteractiveUtil.getElementCellData(gameInfo, it) }
        totalHarvestableCount = cellDataByHarvestableInteractiveElements.size
        currentHarvestedCount = 0
        while (true) {
            gameInfo.logger.closeLog("[$currentHarvestedCount/$totalHarvestableCount]", logItem)
            if (gameInfo.shouldReturnToBank()) {
                if (!TransferItemsToBankTask().run(logItem, gameInfo)) {
                    error("Couldn't transfer items to bank")
                }
            }
            val interactiveElement = getNextElementToHarvest(
                gameInfo = gameInfo,
                itemIdsToHarvest = itemIdsToHarvest,
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
        itemIdsToHarvest: List<Double>,
        cellDataByHarvestableInteractiveElements: Map<InteractiveElement, CompleteCellData>,
        toIgnoreResources: List<InteractiveElement>,
    ): InteractiveElement? {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player")
        val playerCellLocation = gameInfo.dofusBoard.getCell(playerCellId).getCenter().toPointAbsolute(gameInfo)
        val invalidMoveCells = MoveUtil.getInvalidCells(gameInfo).map { it.cellId }
        return cellDataByHarvestableInteractiveElements.filter {
            !toIgnoreResources.contains(it.key)
                && !invalidMoveCells.contains(it.value.cellId)
                && shouldHarvest(it.key, itemIdsToHarvest)
        }.minByOrNull {
            val harvestableCellLocation = gameInfo.dofusBoard.getCell(it.value.cellId)
                .getCenter().toPointAbsolute(gameInfo)
            val dx = playerCellLocation.x - harvestableCellLocation.x
            val dy = playerCellLocation.y - harvestableCellLocation.y
            dx * dx + dy * dy
        }?.key
    }

    private fun shouldHarvest(
        interactiveElement: InteractiveElement,
        itemIdsToHarvest: List<Double>,
    ): Boolean = interactiveElement.enabledSkills.any {
        itemIdsToHarvest.contains(SkillManager.getSkill(it.skillId.toDouble())?.gatheredResourceItem?.id)
    }

    override fun onStarted(): String {
        return "Harvesting all resources ..."
    }

}