package fr.lewon.dofus.bot.core.d2o.managers.entity

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.CharacteristicManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.charac.DofusCharacteristic
import fr.lewon.dofus.bot.core.model.entity.DofusMonster

object MonsterManager : VldbManager {

    private lateinit var monsterById: Map<Double, DofusMonster>

    override fun initManager() {
        monsterById = D2OUtil.getObjects("Monsters").associate {
            val id = it["id"].toString().toDouble()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "UNKNOWN_MONSTER_NAME"
            val isMiniBoss = it["isMiniBoss"].toString().toBoolean()
            val isBoss = it["isBoss"].toString().toBoolean()
            val isQuestMonster = it["isQuestMonster"].toString().toBoolean()
            val spellsIds = it["spells"] as List<Int>
            val spells = spellsIds.mapNotNull { spellId -> SpellManager.getSpell(spellId) }
            val grades = it["grades"] as List<Map<Any, Any>>
            val useSummonSlot = it["useSummonSlot"].toString().toBoolean()
            val canSwitchPos = it["canSwitchPos"].toString().toBoolean()
            val canSwitchPosOnTarget = it["canSwitchPosOnTarget"].toString().toBoolean()
            val canBePushed = it["canBePushed"].toString().toBoolean()
            val baseStats = getBaseStats(grades.lastOrNull())
            id to DofusMonster(
                id, name, isMiniBoss, isBoss, isQuestMonster, spells, baseStats, useSummonSlot,
                canSwitchPos, canSwitchPosOnTarget, canBePushed
            )
        }
    }

    private fun getBaseStats(grade: Map<Any, Any>?): Map<DofusCharacteristic, Int> {
        val baseStats = HashMap<DofusCharacteristic, Int>()
        grade ?: return baseStats
        grade.entries.forEach {
            val keyStr = it.key.toString()
            if (keyStr == "bonusCharacteristics") {
                baseStats.putAll(getBaseStats(it.value as Map<Any, Any>))
            } else {
                val characteristic = CharacteristicManager.getCharacteristicByKeyword(keyStr)
                if (characteristic != null) {
                    baseStats[characteristic] = it.value.toString().toInt()
                }
            }
        }
        return baseStats
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(SpellManager, CharacteristicManager)
    }

    fun getMonsters() = monsterById.values.toList()

    fun getMonster(monsterId: Double) = getMonsterOrNull(monsterId)
        ?: error("No monster for id : $monsterId")

    fun getMonsterOrNull(monsterId: Double) = monsterById[monsterId]
}