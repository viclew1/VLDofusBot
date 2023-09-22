package fr.lewon.dofus.bot.model.characters.paths

class MapsPathByName(store: Map<String, MapsPath> = emptyMap()) :
    HashMap<String, MapsPath>(store.map {
        it.key to it.value.copy(subPaths = it.value.subPaths.map { subPath -> subPath.copy(mapIds = subPath.mapIds.toList()) })
    }.toMap())