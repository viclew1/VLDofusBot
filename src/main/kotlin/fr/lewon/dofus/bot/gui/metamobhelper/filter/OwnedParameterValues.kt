package fr.lewon.dofus.bot.gui.metamobhelper.filter

import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonster

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