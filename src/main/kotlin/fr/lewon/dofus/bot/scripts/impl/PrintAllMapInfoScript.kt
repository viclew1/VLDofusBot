package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.entity.NpcManager
import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.entity.DofusNPC
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.util.network.GameInfo

class PrintAllMapInfoScript : DofusBotScript("Print all map info") {

    override fun getParameters(): List<DofusBotParameter> {
        return emptyList()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Prints all map info, used for development"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val currentMap = gameInfo.currentMap
        val mapD2OInfo = D2OUtil.getObjects("MapPositions").filter { it["id"].toString() == currentMap.id.toString() }
        val mapLogItem = gameInfo.logger.addSubLog("Map : $mapD2OInfo", logItem, 1000)
        logTransitions(mapLogItem, gameInfo)
        logInteractiveElements(mapLogItem, gameInfo)
        logEntityNPCs(mapLogItem, gameInfo)
        logMonsters(mapLogItem, gameInfo)
    }

    private fun logTransitions(mapLogItem: LogItem, gameInfo: GameInfo) {
        val transitionsLogItem = gameInfo.logger.addSubLog("Transitions :", mapLogItem, 100)
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Can't find player cell id")
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: error("Can't find player cell data")
        val currentVertex = WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: error("No vertex found")
        val edges = WorldGraphUtil.getOutgoingEdges(currentVertex)
        edges.forEach { edge ->
            val fromMapId = edge.from.mapId
            val toMapId = edge.to.mapId
            val edgeLogItem = gameInfo.logger.addSubLog("Edge from : $fromMapId / to : $toMapId", transitionsLogItem)
            edge.transitions.forEach {
                gameInfo.logger.addSubLog("Transition ID : ${it.id}", edgeLogItem)
                gameInfo.logger.addSubLog("Transition type : ${it.type}", edgeLogItem)
                gameInfo.logger.addSubLog("Transition direction : ${it.direction}", edgeLogItem)
                gameInfo.logger.addSubLog("Transition map ID : ${it.transitionMapId}", edgeLogItem)
                gameInfo.logger.addSubLog("Transition criterion : ${it.criterion}", edgeLogItem)
            }
        }
    }

    private fun logInteractiveElements(mapLogItem: LogItem, gameInfo: GameInfo) {
        val elementsLogItem = gameInfo.logger.addSubLog("Interactive elements :", mapLogItem, 100)
        gameInfo.interactiveElements.forEach {
            logInteractiveElement(elementsLogItem, gameInfo, it)
        }
    }

    private fun logInteractiveElement(elementsLogItem: LogItem, gameInfo: GameInfo, element: InteractiveElement) {
        val elementLogItem = gameInfo.logger.addSubLog("Element ${element.elementId} :", elementsLogItem, 100)
        gameInfo.logger.addSubLog("Element type ID : ${element.elementTypeId}", elementLogItem)
        element.enabledSkills.forEach {
            val skill = SkillManager.getSkill(it.skillId.toDouble())
            gameInfo.logger.addSubLog("Skill ${it.skillId} : ${skill?.label ?: "Unknown skill label"}", elementLogItem)
        }
    }

    private fun logEntityNPCs(mapLogItem: LogItem, gameInfo: GameInfo) {
        val npcsLogItem = gameInfo.logger.addSubLog("NPCs :", mapLogItem, 100)
        gameInfo.entityIdByNpcId.keys.forEach {
            logNPC(npcsLogItem, gameInfo, NpcManager.getNPC(it.toDouble()))
        }
    }

    private fun logNPC(npcsLogItem: LogItem, gameInfo: GameInfo, npc: DofusNPC) {
        gameInfo.logger.addSubLog("NPC ${npc.id} : ${npc.name}", npcsLogItem)
    }

    private fun logMonsters(mapLogItem: LogItem, gameInfo: GameInfo) {
        val monsterLogItem = gameInfo.logger.addSubLog("Monsters :", mapLogItem, 100)
        gameInfo.monsterInfoByEntityId.values.forEach {
            val monsterId = it.staticInfos.mainCreatureLightInfos.genericId.toDouble()
            logMonster(monsterLogItem, gameInfo, MonsterManager.getMonster(monsterId))
        }
    }

    private fun logMonster(monsterLogItem: LogItem, gameInfo: GameInfo, monster: DofusMonster) {
        gameInfo.logger.addSubLog("Monster ${monster.id} : ${monster.name}", monsterLogItem)
    }
}