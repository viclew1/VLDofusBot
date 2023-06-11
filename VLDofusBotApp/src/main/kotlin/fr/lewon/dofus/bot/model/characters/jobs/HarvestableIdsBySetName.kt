package fr.lewon.dofus.bot.model.characters.jobs

class HarvestableIdsBySetName(store: Map<String, Set<Double>> = emptyMap()) :
    HashMap<String, Set<Double>>(store.map { it.key to HashSet(it.value) }.toMap())