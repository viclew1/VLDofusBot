package fr.lewon.dofus.bot.core.d2o.managers.item

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.model.item.DofusItemEffect

object ItemManager : VldbManager {

    private lateinit var itemById: Map<Double, DofusItem>

    override fun initManager() {
        itemById = D2OUtil.getObjects("Items").associate {
            val id = it["id"].toString().toDouble()
            val possibleEffects = (it["possibleEffects"] as List<Map<String, Any>>).mapNotNull { effect ->
                val effectId = effect["effectId"].toString().toDouble()
                val characteristic = EffectManager.getCharacteristicByEffectId(effectId)
                val min = effect["diceNum"].toString().toInt()
                val max = effect["diceSide"].toString().toInt()
                characteristic?.let { DofusItemEffect(min, max, characteristic) }
            }
            id to DofusItem(id, possibleEffects)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(EffectManager)
    }

    fun getItem(id: Double): DofusItem {
        return itemById[id] ?: error("No item found for id [$id]")
    }

}