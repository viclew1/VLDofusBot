package fr.lewon.dofus.bot.scripts.impl.dev

import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.entity.NpcManager
import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.entity.DofusNPC
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElementSkill
import fr.lewon.dofus.bot.util.network.info.GameInfo

object PrintAllMapInfoScriptBuilder : DofusBotScriptBuilder("Print all map info", true) {

    override fun getParameters(): List<DofusBotParameter> {
        return emptyList()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Prints all map info, used for development"
    }

    override fun doExecuteScript(logItem: LogItem, gameInfo: GameInfo, scriptValues: ScriptValues) {
        val currentMap = gameInfo.currentMap
        val mapD2OInfo =
            D2OUtil.getObjects("MapPositions").filter { it["id"].toString() == currentMap.id.toString() }
        val mapLogItem = gameInfo.logger.addSubLog("Map : $mapD2OInfo", logItem, 1000)
        logTransitions(mapLogItem, gameInfo)
        logInteractiveElements(mapLogItem, gameInfo)
        logEntityNPCs(mapLogItem, gameInfo)
        logMonsters(mapLogItem, gameInfo)
    }

    private fun logTransitions(mapLogItem: LogItem, gameInfo: GameInfo) {
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: return
        val cellData = gameInfo.completeCellDataByCellId[playerCellId]?.cellData
            ?: return
        val currentVertex = WorldGraphUtil.getVertex(gameInfo.currentMap.id, cellData.getLinkedZoneRP())
            ?: return
        val transitionsLogItem = gameInfo.logger.addSubLog("Transitions :", mapLogItem, 100)
        val edges = WorldGraphUtil.getOutgoingEdges(currentVertex)
        edges.forEach { edge ->
            val toMapId = edge.to.mapId
            val edgeLogItem = gameInfo.logger.addSubLog("Edge to : $toMapId", transitionsLogItem)
            edge.transitions.forEach {
                val transitionLogItem = gameInfo.logger.addSubLog("Transition :", edgeLogItem)
                gameInfo.logger.addSubLog("ID : ${it.id}", transitionLogItem)
                gameInfo.logger.addSubLog("type : ${it.type}", transitionLogItem)
                gameInfo.logger.addSubLog("direction : ${it.direction}", transitionLogItem)
                gameInfo.logger.addSubLog("map ID : ${it.transitionMapId.toLong()}", transitionLogItem)
                gameInfo.logger.addSubLog("criterion : ${it.criterion}", transitionLogItem)
                gameInfo.logger.addSubLog("cell ID : ${it.cellId}", transitionLogItem)
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
        val enabledSkillsLogItem = gameInfo.logger.addSubLog("Enabled skills : ", elementLogItem)
        logSkills(gameInfo, element.enabledSkills, enabledSkillsLogItem)
        val disabledSkillsLogItem = gameInfo.logger.addSubLog("Disabled skills : ", elementLogItem)
        logSkills(gameInfo, element.disabledSkills, disabledSkillsLogItem)
    }

    private fun logSkills(gameInfo: GameInfo, skills: ArrayList<InteractiveElementSkill>, skillsLogItem: LogItem) {
        skills.forEach {
            val skill = SkillManager.getSkill(it.skillId.toDouble())
            val label = skill?.label ?: "Unknown skill label"
            gameInfo.logger.addSubLog("Skill ${it.skillId} : $label", skillsLogItem)
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