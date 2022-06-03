package fr.lewon.dofus.bot.gui.metamobhelper.util

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonster
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonsterType
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonsterUpdateState
import fr.lewon.dofus.bot.gui.metamobhelper.monsters.MonsterListContainerPanel
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.ObjectItem
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect.ObjectEffectDice
import fr.lewon.dofus.bot.sniffer.model.types.fight.result.entry.FightResultPlayerListEntry
import fr.lewon.dofus.bot.util.StringUtil

object MetamobMonstersUpdater {

    private val SOUL_STONE_ITEM_IDS = listOf(7010, 10417, 10418)
    private const val MONSTER_STONE_EFFECT_ACTION_ID = 623

    fun addMonsters(playerResult: FightResultPlayerListEntry, monsters: List<DofusMonster>) {
        val playerLoots = playerResult.rewards.objects.filterIndexed { index, _ -> index % 2 == 0 }
        if (playerLoots.intersect(SOUL_STONE_ITEM_IDS).isNotEmpty()) {
            val allMetamobMonsters = getAllMonsters()
            val amountToAddByMonster = HashMap<MetamobMonster, Int>()
            for (monster in monsters) {
                val metamobMonster = allMetamobMonsters
                    .firstOrNull { stringEqualsIgnoreCaseAndAccents(it.name, monster.name) }
                if (metamobMonster != null) {
                    val currentAmount = amountToAddByMonster.computeIfAbsent(metamobMonster) { 0 }
                    amountToAddByMonster[metamobMonster] = currentAmount + 1
                }
            }
            if (amountToAddByMonster.isEmpty()) {
                error("Couldn't add monsters : ${monsters.joinToString(", ") { it.name }}")
            }
            val monsterUpdates = amountToAddByMonster.entries.map {
                val monster = it.key
                val amount = monster.amount + it.value
                buildMonsterUpdate(monster, amount)
            }
            MetamobRequestProcessor.updateMonsters(monsterUpdates)
            MonsterListContainerPanel.refresh()
        }
    }

    fun addMonsters(objectItems: List<ObjectItem>) {
        updateMonstersAmount(objectItems) { current, modifier -> current + modifier }
    }

    fun removeMonsters(objectItems: List<ObjectItem>) {
        updateMonstersAmount(objectItems) { current, modifier -> current - modifier }
    }

    private fun updateMonstersAmount(objectItems: List<ObjectItem>, amountSumCalculator: (Int, Int) -> Int) {
        val allMonsters = getAllMonsters()
        val amountByMonster = getAmountByMonster(allMonsters, objectItems)
        if (amountByMonster.isEmpty()) {
            return
        }
        val monsterUpdates = amountByMonster.entries.map {
            val monster = it.key
            val amount = amountSumCalculator(monster.amount, it.value)
            buildMonsterUpdate(monster, amount)
        }
        MetamobRequestProcessor.updateMonsters(monsterUpdates)
        MonsterListContainerPanel.refresh()
    }

    fun cleanAndUpdateMonsters(objectItems: List<ObjectItem>) {
        val allMonsters = getAllMonsters()
        val amountByMonster = getAmountByMonster(allMonsters, objectItems)
        val monsterUpdates = allMonsters.map {
            val amount = amountByMonster[it] ?: 0
            buildMonsterUpdate(it, amount)
        }
        MetamobRequestProcessor.updateMonsters(monsterUpdates)
        MonsterListContainerPanel.refresh()
    }

    private fun getAmountByMonster(
        monsters: List<MetamobMonster>,
        objectItems: List<ObjectItem>,
    ): Map<MetamobMonster, Int> {
        val amountByMonster = HashMap<MetamobMonster, Int>()
        for (objectItem in objectItems) {
            val monstersStored = objectItem.effects.filterIsInstance<ObjectEffectDice>()
                .filter { it.actionId == MONSTER_STONE_EFFECT_ACTION_ID }
            for (monsterStored in monstersStored) {
                val monsterName = MonsterManager.getMonster(monsterStored.diceConst.toDouble()).name.lowercase()
                val monster = monsters.firstOrNull { stringEqualsIgnoreCaseAndAccents(it.name, monsterName) }
                if (monster != null) {
                    val currentAmount = amountByMonster.computeIfAbsent(monster) { 0 }
                    amountByMonster[monster] = currentAmount + 1
                }
            }
        }
        return amountByMonster
    }

    private fun stringEqualsIgnoreCaseAndAccents(str1: String, str2: String): Boolean {
        return StringUtil.removeAccents(str1).lowercase() == StringUtil.removeAccents(str2).lowercase()
    }

    private fun getAllMonsters(): List<MetamobMonster> {
        return MetamobRequestProcessor.getAllMonsters()
            ?: error("Couldn't get metamob monsters")
    }

    private fun buildMonsterUpdate(monster: MetamobMonster, amount: Int): MetamobMonsterUpdate {
        val state = when {
            monster.type == MetamobMonsterType.MONSTER -> MetamobMonsterUpdateState.NONE
            amount == 0 -> MetamobMonsterUpdateState.SEARCH
            amount > 1 -> MetamobMonsterUpdateState.OFFER
            else -> MetamobMonsterUpdateState.NONE
        }
        return MetamobMonsterUpdate(monster.id, state, amount)
    }

}