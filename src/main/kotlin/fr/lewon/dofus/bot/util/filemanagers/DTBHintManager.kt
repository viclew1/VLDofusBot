package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.model.hint.Hint
import fr.lewon.dofus.bot.model.hint.HintsByMapWithDirection
import fr.lewon.dofus.bot.model.hint.MoveHints
import fr.lewon.dofus.bot.model.hint.PhorrorHint
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.model.maps.DofusMapWithDirection
import fr.lewon.dofus.bot.util.DTBRequestProcessor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object DTBHintManager {

    private val hints: HintsByMapWithDirection
    private val hintsIdsByName: HashMap<String, Int>

    private val hintsFile: File = File("config/hints")
    private val hintsIdsByNameFile: File = File("config/hintsIdsByName")

    init {
        if (hintsFile.exists() && hintsIdsByNameFile.exists()) {
            hints = ObjectMapper().readValue(hintsFile)
            hintsIdsByName = ObjectMapper().readValue(hintsIdsByNameFile)
        } else {
            hints = HintsByMapWithDirection()
            hintsIdsByName = DTBRequestProcessor.getAllHintIdsByName()
            hintsIdsByName[PhorrorHint.name] = PhorrorHint.id
            saveHints()
            saveHintsIdsByName()
        }
    }


    fun editHints(function: (HintsByMapWithDirection) -> Unit) {
        function.invoke(hints)
        saveHints()
    }

    private fun saveHints() {
        save(hintsFile, hints)
    }

    private fun saveHintsIdsByName() {
        save(hintsIdsByNameFile, hintsIdsByName)
    }

    private fun save(file: File, content: Any) {
        with(OutputStreamWriter(FileOutputStream(file, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(content))
            close()
        }
    }

    fun getHint(map: DofusMap, direction: Direction, hintLabel: String): Hint? {
        val hintId = hintsIdsByName[hintLabel] ?: error("Couldn't find id for hint [$hintLabel]")
        return getHint(map, direction, hintId)
    }

    fun getHint(map: DofusMap, direction: Direction, hintId: Int): Hint? {
        val mapWithDirection = DofusMapWithDirection(map, direction)
        var hints = getHints(mapWithDirection)
        if (hints == null) {
            synchronize(map)
            saveHints()
            saveHintsIdsByName()
            hints = getHints(mapWithDirection)
        }
        return hints?.firstOrNull { hintId == it.n }
    }

    private fun getHints(mapWithDirection: DofusMapWithDirection): List<Hint>? {
        return hints[mapWithDirection]
    }

    private fun synchronize(map: DofusMap) {
        for (dir in Direction.values()) {
            val hintsByDirection = DTBRequestProcessor.getHints(map.posX, map.posY, dir, map.worldMap) ?: continue
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