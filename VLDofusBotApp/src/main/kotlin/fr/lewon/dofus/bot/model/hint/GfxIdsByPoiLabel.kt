package fr.lewon.dofus.bot.model.hint

class GfxIdsByPoiLabel(store: Map<String, HashSet<Int>> = emptyMap()) :
    HashMap<String, HashSet<Int>>(store.map { it.key to HashSet(it.value) }.toMap())