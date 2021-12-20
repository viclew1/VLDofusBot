package fr.lewon.dofus.bot.game.fight.operations

class UsesStore : HashMap<String, HashMap<Double, Int>>() {

    fun getTotalUses(spellKey: String): Int {
        return this[spellKey]?.values?.sum() ?: 0
    }

    fun getUsesOnTarget(spellKey: String, targetId: Double): Int {
        return this[spellKey]?.get(targetId) ?: 0
    }

    fun deepCopy(): UsesStore {
        val copy = UsesStore()
        for (e in entries) {
            copy[e.key] = HashMap(e.value)
        }
        return copy
    }

}