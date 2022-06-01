package fr.lewon.dofus.bot.gui.metamobhelper.model

import com.fasterxml.jackson.annotation.JsonValue

enum class MetamobMonsterUpdateState(private val label: String) {
    SEARCH("recherche"),
    OFFER("propose"),
    NONE("aucun");

    @JsonValue
    fun getMetamobLabel(): String {
        return label
    }

    companion object {
        fun fromLabel(label: String): MetamobMonsterUpdateState {
            return values().firstOrNull { it.label == label }
                ?: error("Invalid label : $label")
        }
    }
}