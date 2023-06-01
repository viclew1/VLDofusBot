package fr.lewon.dofus.bot.util.external.metamob

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.main.metamob.MetamobHelperUIUtil
import fr.lewon.dofus.bot.sniffer.model.types.game.context.fight.FightResultPlayerListEntry
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItem
import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectDice
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterUpdateState
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.ids.EffectIds
import fr.lewon.dofus.bot.util.ids.ItemIds
import java.util.concurrent.locks.ReentrantLock

object MetamobMonstersHelper {

    private val lock = ReentrantLock()

    fun isMetamobConfigured(): Boolean {
        val metamobConfig = MetamobConfigManager.readConfig()
        val username = metamobConfig.metamobUsername ?: ""
        val uniqueId = metamobConfig.metamobUniqueID ?: ""
        return username.isNotBlank() && uniqueId.isNotBlank()
    }

    fun getMetamobMonster(monster: DofusMonster, metamobMonsters: List<MetamobMonster>): MetamobMonster? {
        return metamobMonsters.firstOrNull { stringEqualsIgnoreCaseAndAccents(it.name, monster.name) }
    }

    fun addMonsters(playerResult: FightResultPlayerListEntry, monsters: List<DofusMonster>) {
        return lock.executeSyncOperation {
            if (playerResult.rewards.objects.any { ItemIds.SOUL_STONE_ITEM_IDS.contains(it.objectId) }) {
                val allMetamobMonsters = getAllMonsters()
                val amountToAddByMonster = HashMap<MetamobMonster, Int>()
                for (monster in monsters) {
                    val metamobMonster = getMetamobMonster(monster, allMetamobMonsters)
                    if (metamobMonster != null) {
                        val currentAmount = amountToAddByMonster.computeIfAbsent(metamobMonster) { 0 }
                        amountToAddByMonster[metamobMonster] = currentAmount + 1
                    }
                }
                if (amountToAddByMonster.isNotEmpty()) {
                    val monsterUpdates = amountToAddByMonster.entries.map {
                        buildMonsterUpdate(it.key, it.value, UpdateOperation.ADD)
                    }
                    MetamobRequestProcessor.updateMonsters(monsterUpdates)
                    MetamobHelperUIUtil.refreshMonsters()
                }
            }
        }
    }

    fun addMonsters(objectItems: List<ObjectItem>) {
        return lock.executeSyncOperation {
            updateMonstersAmount(objectItems, UpdateOperation.ADD)
        }
    }

    fun removeMonsters(objectItems: List<ObjectItem>) {
        return lock.executeSyncOperation {
            updateMonstersAmount(objectItems, UpdateOperation.REMOVE)
        }
    }

    fun addAndRemoveMonsters(toAddObjectItems: List<ObjectItem>, toRemoveObjectItems: List<ObjectItem>) {
        return lock.executeSyncOperation {
            addMonsters(toAddObjectItems)
            removeMonsters(toRemoveObjectItems)
        }
    }

    private fun updateMonstersAmount(objectItems: List<ObjectItem>, updateOperation: UpdateOperation) {
        val allMonsters = getAllMonsters()
        val amountByMonster = getAmountByMonster(allMonsters, objectItems)
        if (amountByMonster.isEmpty()) {
            return
        }
        val monsterUpdates = amountByMonster.entries.map {
            buildMonsterUpdate(it.key, it.value, updateOperation)
        }
        MetamobRequestProcessor.updateMonsters(monsterUpdates)
        MetamobHelperUIUtil.refreshMonsters()
    }

    fun cleanAndUpdateMonsters(objectItems: List<ObjectItem>) {
        return lock.executeSyncOperation {
            val allMonsters = getAllMonsters()
            val amountByMonster = getAmountByMonster(allMonsters, objectItems)
            val monsterUpdates = allMonsters.map {
                val amount = amountByMonster[it] ?: 0
                buildMonsterUpdate(it, amount, UpdateOperation.REPLACE)
            }
            MetamobRequestProcessor.updateMonsters(monsterUpdates)
            MetamobHelperUIUtil.refreshMonsters()
        }
    }

    fun updateMonstersStatuses() = lock.executeSyncOperation {
        val allMonsters = getAllMonsters()
        val monsterUpdates = allMonsters.map {
            buildMonsterUpdate(it, it.amount, UpdateOperation.REPLACE)
        }
        MetamobRequestProcessor.updateMonsters(monsterUpdates)
        MetamobHelperUIUtil.refreshMonsters()
    }

    private fun getAmountByMonster(
        monsters: List<MetamobMonster>,
        objectItems: List<ObjectItem>,
    ): Map<MetamobMonster, Int> {
        val amountByMonster = HashMap<MetamobMonster, Int>()
        for (objectItem in objectItems) {
            val monstersStored = objectItem.effects.filterIsInstance<ObjectEffectDice>()
                .filter { it.actionId == EffectIds.MONSTER_STONE_EFFECT_EFFECT_ID }
            for (monsterStored in monstersStored) {
                val monsterName = MonsterManager.getMonster(monsterStored.diceConst.toDouble()).name.lowercase()
                val monster = monsters.firstOrNull { stringEqualsIgnoreCaseAndAccents(it.name, monsterName) }
                if (monster != null) {
                    val currentAmount = amountByMonster.computeIfAbsent(monster) { 0 }
                    amountByMonster[monster] = currentAmount + objectItem.quantity
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

    private fun buildMonsterUpdate(
        monster: MetamobMonster,
        amount: Int,
        updateOperation: UpdateOperation
    ): MetamobMonsterUpdate {
        val config = MetamobConfigManager.readConfig()
        val simultaneousOchers = config.getSafeSimultaneousOchers()
        val disableStatusUpdate = config.disableStatusUpdate
        val totalAmount = updateOperation.getTotalAmount(monster, amount)
        val state = when {
            disableStatusUpdate -> MetamobMonsterUpdateState.NONE
            monster.type != MetamobMonsterType.ARCHMONSTER -> MetamobMonsterUpdateState.NONE
            totalAmount < simultaneousOchers -> MetamobMonsterUpdateState.SEARCH
            totalAmount > simultaneousOchers -> MetamobMonsterUpdateState.OFFER
            else -> MetamobMonsterUpdateState.NONE
        }
        return MetamobMonsterUpdate(monster.id, state, "${updateOperation.prefix}$amount")
    }

    private enum class UpdateOperation(val prefix: String, private val amountCalculator: (Int, Int) -> Int) {
        REPLACE("", { _, modifiedAmount -> modifiedAmount }),
        ADD("+", { currentAmount, modifiedAmount -> currentAmount + modifiedAmount }),
        REMOVE("-", { currentAmount, modifiedAmount -> currentAmount - modifiedAmount });

        fun getTotalAmount(monster: MetamobMonster, amount: Int): Int {
            return amountCalculator(monster.amount, amount)
        }
    }

}