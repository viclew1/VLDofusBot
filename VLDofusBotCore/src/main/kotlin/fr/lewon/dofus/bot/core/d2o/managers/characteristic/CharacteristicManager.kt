package fr.lewon.dofus.bot.core.d2o.managers.characteristic

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.charac.DofusCharacteristic

object CharacteristicManager : VldbManager {

    private lateinit var characteristicByKeyword: HashMap<String, DofusCharacteristic>
    private lateinit var characteristicById: HashMap<Int, DofusCharacteristic>

    override fun initManager() {
        characteristicByKeyword = HashMap()
        characteristicById = HashMap()
        D2OUtil.getObjects("Characteristics").map {
            val id = it["id"].toString().toInt()
            val order = it["order"].toString().toInt()
            val keyWord = it["keyword"].toString()
            val categoryId = it["categoryId"].toString().toInt()
            DofusCharacteristic(id, order, categoryId, keyWord)
        }.forEach {
            characteristicById[it.id] = it
            characteristicByKeyword[it.keyWord] = it
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getCharacteristic(id: Int): DofusCharacteristic? {
        return characteristicById[id]
    }

    fun getCharacteristicByKeyword(keyWord: String): DofusCharacteristic? {
        return characteristicByKeyword[keyWord]
    }

}