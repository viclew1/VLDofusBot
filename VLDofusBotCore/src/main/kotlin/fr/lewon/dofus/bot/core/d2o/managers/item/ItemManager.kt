package fr.lewon.dofus.bot.core.d2o.managers.item

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.item.DofusItem
import fr.lewon.dofus.bot.core.model.item.DofusItemEffect

object ItemManager : VldbManager {

    private lateinit var itemById: Map<Double, DofusItem>

    override fun initManager() {
        itemById = D2OUtil.getObjects("Items").associate {
            val id = it["id"].toString().toDouble()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "UNKNOWN ITEM NAME"
            val iconId = it["iconId"].toString().toInt()
            val typeId = it["typeId"].toString().toInt()
            val possibleEffects = (it["possibleEffects"] as List<Map<String, Any>>).map { possibleEffect ->
                val effectId = possibleEffect["effectId"].toString().toInt()
                val effect = EffectManager.getEffect(effectId)
                val min = possibleEffect["diceNum"].toString().toInt()
                val max = possibleEffect["diceSide"].toString().toInt()
                DofusItemEffect(min, max, effect)
            }.sortedBy { itemEffect ->
                val characteristic = itemEffect.effect.characteristic
                if (characteristic != null) {
                    characteristic.categoryId * Short.MAX_VALUE.toInt() + characteristic.order
                } else Int.MAX_VALUE
            }
            id to DofusItem(id, name, iconId, typeId, possibleEffects)
        }
    }

    override fun getNeededManagers() = listOf(EffectManager)

    fun getItem(id: Double) = itemById[id] ?: error("No item found for id [$id]")

    fun getItems() = itemById.values.toList()

}