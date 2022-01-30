package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.d2o.managers.entity.NpcManager
import fr.lewon.dofus.bot.core.d2o.managers.interactive.SkillManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.entity.DofusNPC
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
        val mapLogItem = gameInfo.logger.addSubLog("Map : ${currentMap.id}", logItem, 1000)
        logInteractiveElements(mapLogItem, gameInfo)
        logEntityNPCs(mapLogItem, gameInfo)
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
}