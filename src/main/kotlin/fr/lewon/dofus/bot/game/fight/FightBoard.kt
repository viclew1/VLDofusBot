package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.core.utils.LockUtils
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai.GameFightMonsterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named.GameFightCharacterInformations
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSpellManager
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class FightBoard(private val gameInfo: GameInfo) {

    private val lock = ReentrantLock()
    private val dofusBoard = gameInfo.dofusBoard
    private val fightersById = HashMap<Double, Fighter>()
    val deadFighters = ArrayList<Fighter>()

    fun move(fromCellId: Int, toCellId: Int) {
        LockUtils.executeSyncOperation(lock) {
            getFighter(fromCellId)?.let { move(it, toCellId) }
        }
    }

    fun move(fighterId: Double, toCellId: Int) {
        LockUtils.executeSyncOperation(lock) {
            fightersById[fighterId]?.let { move(it, toCellId) }
        }
    }

    fun move(fighter: Fighter, toCellId: Int) {
        LockUtils.executeSyncOperation(lock) {
            move(fighter, dofusBoard.getCell(toCellId))
        }
    }

    fun move(fighter: Fighter, toCell: DofusCell) {
        LockUtils.executeSyncOperation(lock) {
            fighter.cell = toCell
        }
    }

    fun resetFighters() {
        LockUtils.executeSyncOperation(lock) {
            fightersById.clear()
        }
    }

    fun killFighter(id: Double) {
        LockUtils.executeSyncOperation(lock) {
            fightersById[id]?.let { deadFighters.add(it) }
            fightersById.remove(id)
        }
    }

    fun createOrUpdateFighter(fighterInfo: GameFightFighterInformations): Fighter {
        return LockUtils.executeSyncOperation(lock) {
            val fighterId = fighterInfo.contextualId
            val cellId = fighterInfo.spawnInfo.informations.disposition.cellId
            val spells = getSpellLevels(gameInfo, fighterInfo, fighterId)
            val cell = dofusBoard.getCell(cellId)
            val fighter = fightersById.computeIfAbsent(fighterId) {
                Fighter(cell, fighterId, fighterInfo)
            }
            fighter.spells = spells
            fighter.teamId = fighterInfo.spawnInfo.teamId
            move(fighter, cell)
            fighter
        }
    }

    private fun getSpellLevels(
        gameInfo: GameInfo,
        fighterInfo: GameFightFighterInformations,
        fighterId: Double
    ): List<DofusSpellLevel> {
        return when {
            fighterInfo is GameFightMonsterInformations -> {
                val spells = MonsterManager.getMonster(fighterInfo.creatureGenericId.toDouble()).spells
                getSpellLevels(spells, fighterInfo.creatureLevel)
            }
            fighterInfo is GameFightCharacterInformations && fighterId == gameInfo.playerId -> {
                val characterSpells = CharacterSpellManager.getSpells(gameInfo.character)
                val spellIds = characterSpells.mapNotNull { it.spellId }
                val spells = spellIds.mapNotNull { SpellManager.getSpell(it) }
                getSpellLevels(spells, fighterInfo.level)
            }
            else -> emptyList()
        }
    }

    private fun getSpellLevels(spells: List<DofusSpell>, level: Int): List<DofusSpellLevel> {
        return spells.mapNotNull { getSpellLevel(it, level) }
    }

    private fun getSpellLevel(spell: DofusSpell, level: Int): DofusSpellLevel? {
        return spell.levels.filter { it.minPlayerLevel <= level }.maxByOrNull { it.minPlayerLevel }
    }

    fun updateFighterCharacteristics(fighterId: Double, characteristics: List<CharacterCharacteristic>) {
        LockUtils.executeSyncOperation(lock) {
            fightersById[fighterId]?.let { updateFighterCharacteristics(it, characteristics) }
        }
    }

    fun updateFighterCharacteristics(fighter: Fighter, characteristics: List<CharacterCharacteristic>) {
        LockUtils.executeSyncOperation(lock) {
            characteristics.forEach { fighter.statsById[it.characteristicId] = it }
        }
    }

    fun getPlayerFighter(): Fighter? {
        return LockUtils.executeSyncOperation(lock) {
            fightersById[gameInfo.playerId]
        }
    }

    fun getEnemyFighters(): List<Fighter> {
        return LockUtils.executeSyncOperation(lock) {
            getFighters(true)
        }
    }

    fun getClosestEnemy(): Fighter? {
        val playerFighter = getPlayerFighter() ?: return null
        return getEnemyFighters().minByOrNull { dofusBoard.getDist(playerFighter.cell, it.cell) }
    }

    fun getAlliedFighters(): List<Fighter> {
        return LockUtils.executeSyncOperation(lock) {
            getFighters(false)
        }
    }

    fun getAllFighters(withSummons: Boolean = true): List<Fighter> {
        return LockUtils.executeSyncOperation(lock) {
            fightersById.values.toList().filter { withSummons || !it.isSummon() }
        }
    }

    private fun getFighters(enemy: Boolean): List<Fighter> {
        return fightersById.values.filter { isFighterEnemy(it) == enemy }
    }

    fun isFighterEnemy(fighter: Fighter): Boolean {
        return getPlayerFighter()?.let {
            it.teamId != fighter.teamId
        } ?: true
    }

    fun isFighterHere(cell: DofusCell): Boolean {
        return LockUtils.executeSyncOperation(lock) {
            getFighter(cell) != null
        }
    }

    fun getFighter(cell: DofusCell): Fighter? {
        return LockUtils.executeSyncOperation(lock) {
            getFighter(cell.cellId)
        }
    }

    fun getFighter(cellId: Int): Fighter? {
        return LockUtils.executeSyncOperation(lock) {
            fightersById.values.firstOrNull { it.cell.cellId == cellId }
        }
    }

    fun getFighterById(fighterId: Double): Fighter? {
        return LockUtils.executeSyncOperation(lock) {
            fightersById[fighterId]
        }
    }

    fun lineOfSight(fromCell: Int, toCell: Int): Boolean {
        return lineOfSight(dofusBoard.getCell(fromCell), dofusBoard.getCell(toCell))
    }

    fun lineOfSight(fromCell: DofusCell, toCell: DofusCell): Boolean {
        return LockUtils.executeSyncOperation(lock) {
            doLineOfSight(fromCell, toCell)
        }
    }

    private fun doLineOfSight(fromCell: DofusCell, toCell: DofusCell): Boolean {
        val x0 = fromCell.col
        val y0 = fromCell.row
        val x1 = toCell.col
        val y1 = toCell.row

        var dx = abs(x1 - x0)
        var dy = abs(y1 - y0)
        var x = x0
        var y = y0
        var n = -1 + dx + dy
        val xInc = if (x1 > x0) 1 else -1
        val yInc = if (y1 > y0) 1 else -1
        var error = dx - dy
        dx *= 2
        dy *= 2

        when {
            error > 0 -> {
                x += xInc
                error -= dy
            }
            error < 0 -> {
                y += yInc
                error += dx
            }
            else -> {
                x += xInc
                error -= dy
                y += yInc
                error += dx
                n--
            }
        }

        while (n > 0) {
            val cell = dofusBoard.getCell(x, y) ?: error("Cell [$x ; $y] does not exist")
            if (cell.isWall() || cell != fromCell && cell != toCell && isFighterHere(cell)) {
                return false
            }
            when {
                error > 0 -> {
                    x += xInc
                    error -= dy
                }
                error < 0 -> {
                    y += yInc
                    error += dx
                }
                else -> {
                    x += xInc
                    error -= dy
                    y += yInc
                    error += dx
                    n--
                }
            }
            n--
        }
        return true
    }

    fun getMoveCellsWithMpUsed(range: Int, fromCell: DofusCell): List<Pair<DofusCell, Int>> {
        return LockUtils.executeSyncOperation(lock) {
            val cellsWithMpUsed = ArrayList<Pair<DofusCell, Int>>()
            cellsWithMpUsed.add(fromCell to 0)
            val accessibleCells = getMoveCellsWithMpUsed(range, listOf(fromCell))
            cellsWithMpUsed.addAll(accessibleCells)
            cellsWithMpUsed
        }
    }

    fun getMoveCellsWithMpUsed(range: Int, initialFrontier: List<DofusCell>): List<Pair<DofusCell, Int>> {
        return LockUtils.executeSyncOperation(lock) {
            val cellsWithMpUsed = ArrayList<Pair<DofusCell, Int>>()
            val explored = ArrayList<Int>()
            explored.addAll(initialFrontier.map { it.cellId })
            var frontier = ArrayList<DofusCell>()
            frontier.addAll(initialFrontier)
            var dist = 1
            for (i in 0 until range) {
                val newFrontier = ArrayList<DofusCell>()
                for (cell in frontier) {
                    for (neighbor in cell.neighbors) {
                        if (!explored.contains(neighbor.cellId) && !isFighterHere(neighbor) && neighbor.isAccessible()) {
                            explored.add(neighbor.cellId)
                            newFrontier.add(neighbor)
                            cellsWithMpUsed.add(neighbor to dist)
                        }
                    }
                }
                dist++
                frontier = newFrontier
            }
            cellsWithMpUsed
        }
    }

    fun deepCopy(): FightBoard {
        return LockUtils.executeSyncOperation(lock) {
            FightBoard(gameInfo).also {
                it.fightersById.putAll(fightersById.entries.map { e -> e.key to e.value.deepCopy() })
            }
        }
    }

}