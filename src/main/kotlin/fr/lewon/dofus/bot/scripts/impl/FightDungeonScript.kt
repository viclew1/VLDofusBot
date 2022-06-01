package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.dungeon.Dungeons
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightDungeonTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

class FightDungeonScript : DofusBotScript("Fight dungeon") {

    private var count = 0

    companion object {
        private val DUNGEON_BY_NAME = mapOf(
            "Rats Brakmar" to Dungeons.BRAKMAR_RATS_DUNGEON,
            "Draegnerys" to Dungeons.DRAEGNERYS_DUNGEON,
            "Bouftou" to Dungeons.BOUFTOU_DUNGEON,
            "Champs" to Dungeons.CHAMPS_DUNGEON,
            "Ensablé" to Dungeons.ENSABLE_DUNGEON
        )
    }

    private val dungeonParameter = DofusBotParameter(
        "Dungeon",
        "The dungeon you want to farm",
        DUNGEON_BY_NAME.keys.first(),
        DofusBotParameterType.CHOICE,
        DUNGEON_BY_NAME.keys.toList()
    )

    override fun getParameters(): List<DofusBotParameter> {
        return listOf(dungeonParameter)
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return listOf(DofusBotScriptStat("Count", count.toString()))
    }

    override fun getDescription(): String {
        return "Fights in the dungeon until you run out of keys"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
        val dungeonName = dungeonParameter.value
        val dungeon = DUNGEON_BY_NAME[dungeonName] ?: error("Invalid dungeon : $dungeonName")
        count = 0
        while (FightDungeonTask(dungeon).run(logItem, gameInfo)) {
            count++
        }
    }

}