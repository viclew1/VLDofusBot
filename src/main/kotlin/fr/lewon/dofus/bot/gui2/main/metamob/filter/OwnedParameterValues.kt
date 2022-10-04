package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

enum class OwnedParameterValues(val label: String, val monsterMatchesFun: (MetamobMonster) -> Boolean) {
    ANY("Any", { true }),
    YES("Yes", { it.amount > 0 }),
    NO("No", { it.amount <= 0 });

    companion object {
        fun fromLabel(label: String): OwnedParameterValues {
            return values().firstOrNull { it.label == label }
                ?: error("Invalid label : $label")
        }
    }
}