package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.model.hint.Hint
import fr.lewon.dofus.bot.model.hint.MoveHints
import fr.lewon.dofus.bot.model.hint.PhorrorHint
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.model.maps.DofusMapWithDirection
import fr.lewon.dofus.bot.util.DTBRequestProcessor
import fr.lewon.dofus.bot.util.math.LevenshteinDistanceUtil

object DTBHintManager {

    private val hints = HashMap<DofusMapWithDirection, ArrayList<Hint>>()
    private val hintsIdsByName = DTBRequestProcessor.getAllHintIdsByName()

    init {
        hintsIdsByName[PhorrorHint.name] = PhorrorHint.id
    }


    fun getHint(map: DofusMap, direction: Direction, hintLabel: String): Hint? {
        var hintId = hintsIdsByName[hintLabel]
        if (hintId == null) {
            val closestLabel = LevenshteinDistanceUtil.getClosestString(hintLabel, hintsIdsByName.keys.toList())
            hintId = hintsIdsByName[closestLabel]
        }
        hintId ?: error("Couldn't find id for hint [$hintLabel]")
        return getHint(map, direction, hintId)
    }


    fun getHint(map: DofusMap, direction: Direction, hintId: Int): Hint? {
        val mapWithDirection = DofusMapWithDirection(map, direction)
        var hints = getHints(mapWithDirection)
        if (hints == null) {
            synchronize(map)
            hints = getHints(mapWithDirection)
        }
        return hints?.firstOrNull { hintId == it.n }
    }

    private fun getHints(mapWithDirection: DofusMapWithDirection): List<Hint>? {
        return hints[mapWithDirection]
    }

    private fun synchronize(map: DofusMap) {
        for (dir in Direction.values()) {
            val hintsByDirection = DTBRequestProcessor.getHints(map.posX, map.posY, dir, map.worldMap)
            synchronize(map, dir, hintsByDirection)
        }
    }

    private fun synchronize(map: DofusMap, direction: Direction, newMoveHints: MoveHints) {
        val mapWithDirection = DofusMapWithDirection(map, direction)
        val hintsForMapWithDirection = hints.computeIfAbsent(mapWithDirection) { ArrayList() }
        val hintsIds = hintsForMapWithDirection.map { it.n }
        for (hint in newMoveHints.hints) {
            if (!hintsIds.contains(hint.n)) {
                hintsForMapWithDirection.add(hint)
            }
        }
    }
}