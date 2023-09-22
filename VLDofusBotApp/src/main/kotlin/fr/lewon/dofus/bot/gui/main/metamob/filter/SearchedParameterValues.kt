package fr.lewon.dofus.bot.gui.main.metamob.filter

import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster

enum class SearchedParameterValues(val label: String, val monsterMatchesFun: (MetamobMonster) -> Boolean) {
    ANY("Any", { true }),
    SEARCHED("Searched", { it.searched > 0 }),
    OFFERED("Offered", { it.offered > 0 });

    companion object {

        fun fromLabel(label: String): SearchedParameterValues {
            return SearchedParameterValues.entries.firstOrNull { it.label == label }
                ?: error("Invalid label : $label")
        }
    }
}