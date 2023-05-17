package fr.lewon.dofus.bot.util.external.metamob.model

import com.fasterxml.jackson.annotation.JsonValue

enum class MetamobMonsterType(val label: String, val displayLabel: String) {
    ANY("IMPOSSIBLE", "Any"),
    ARCHMONSTER("archimonstre", "Archmonster"),
    MONSTER("monstre", "Monster"),
    BOSS("boss", "Boss");

    @JsonValue
    fun getMetamobLabel(): String {
        return label
    }

    companion object {
        fun fromDisplayLabel(displayLabel: String): MetamobMonsterType {
            return MetamobMonsterType.values().firstOrNull { it.displayLabel == displayLabel }
                ?: error("Invalid display label : $displayLabel")
        }
    }
}