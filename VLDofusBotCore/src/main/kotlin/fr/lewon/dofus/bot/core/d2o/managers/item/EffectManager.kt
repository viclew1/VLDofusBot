package fr.lewon.dofus.bot.core.d2o.managers.item

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.CharacteristicManager
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.item.DofusEffect

object EffectManager : VldbManager {

    private lateinit var effectById: Map<Int, DofusEffect>

    override fun initManager() {
        effectById = D2OUtil.getObjects("Effects").associate {
            val id = it["id"].toString().toInt()
            val characteristicId = it["characteristic"].toString().toInt()
            val characteristic = CharacteristicManager.getCharacteristic(characteristicId)
            val descriptionId = it["descriptionId"].toString().toInt()
            val description = I18NUtil.getLabel(descriptionId) ?: "UNKNOWN_EFFECT_NAME"
            val useDice = it["useDice"].toString().toBoolean()
            val boost = it["boost"].toString().toBoolean()
            val active = it["active"].toString().toBoolean()
            val operator = it["operator"].toString()
            id to DofusEffect(id, description, characteristic, useDice, boost, active, operator)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(CharacteristicManager)
    }

    fun getEffect(effectId: Int) = effectById[effectId] ?: error("No effect for ID : $effectId")

    fun getEffects() = effectById.values.toList()

}
