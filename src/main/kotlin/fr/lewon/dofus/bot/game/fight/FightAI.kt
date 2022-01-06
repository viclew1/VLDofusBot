package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.game.fight.operations.UsesStore
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import kotlin.math.abs
import kotlin.math.sign

class FightAI(
    private val dofusBoard: DofusBoard,
    private val fightBoard: FightBoard,
    private val playerFighter: Fighter,
    private val playerRange: Int,
    private val initialDepth: Int,
    spells: List<SpellCombination>,
    private val aiComplement: AIComplement
) {

    private val mpBuffCombinations = spells.filter { it.type == SpellType.MP_BUFF }
    private val attackSpellCombinations = spells.filter { it.type == SpellType.ATTACK }
        .sortedByDescending { it.aiWeight }
    private val gapCloserCombinations = spells.filter { it.type == SpellType.GAP_CLOSER }

    private val usesStore = UsesStore()
    private val cdBySpellKey = HashMap<String, Int>()
    private val mpByFighterId = HashMap<Double, Int>()
    private var initialPlayerAp = 0

    fun selectStartCell(): DofusCell? {
        val tempFightBoard = fightBoard.clone()
        val idealDist = aiComplement.getIdealDistance(playerFighter, attackSpellCombinations, playerRange)
        return dofusBoard.startCells.map {
            val tempPlayerFighter = tempFightBoard.getPlayerFighter()
                ?: error("Player fighter not found")
            tempFightBoard.move(tempPlayerFighter, it)
            val closestEnemy = tempFightBoard.getClosestEnemy() ?: error("Closest enemy not found")
            it to (dofusBoard.getPathLength(it, closestEnemy.cell)
                ?: dofusBoard.getDist(it, closestEnemy.cell))
        }.minByOrNull { abs(idealDist - it.second) }?.first
    }

    fun onNewTurn() {
        fightBoard.getAllFighters().forEach {
            mpByFighterId[it.id] = FighterCharacteristic.MP.getFighterCharacteristicValue(it)
        }
        initialPlayerAp = FighterCharacteristic.AP.getFighterCharacteristicValue(playerFighter)
        usesStore.clear()
        val toRemove = ArrayList<String>()
        cdBySpellKey.entries.forEach {
            val newValue = it.value - 1
            if (newValue > 0) {
                cdBySpellKey[it.key] = newValue
            } else {
                toRemove.add(it.key)
            }
        }
        toRemove.forEach { cdBySpellKey.remove(it) }
    }

    fun getNextOperation(): FightOperation? {
        val playerAp = FighterCharacteristic.AP.getFighterCharacteristicValue(playerFighter)
        val playerMP = FighterCharacteristic.MP.getFighterCharacteristicValue(playerFighter)
        val enemies = getEnemyPlayers(fightBoard)

        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val accessibleCells = fightBoard.getMoveCellsWithMpUsed(playerMP, playerPosition)
        val bestMoveDest = selectBestMoveDest(playerAp, playerPosition, accessibleCells)
        val bestMoveDestScore = bestMoveDest?.value ?: 0

        selectBestMpBuff(playerAp, accessibleCells.map { it.first }, bestMoveDestScore)?.let {
            useSpell(fightBoard, it.spell, usesStore, cdBySpellKey, playerFighter.cell)
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, it.spell.keys)
        }
        bestMoveDest?.takeIf { it.target.cellId != playerFighter.cell.cellId }?.let {
            return FightOperation(FightOperationType.MOVE, it.target.cellId)
        }
        selectBestGapCloser(playerAp)?.takeIf { it.target.cellId != playerFighter.cell.cellId }?.let {
            useSpell(fightBoard, it.spell, usesStore, cdBySpellKey, it.target)
            return FightOperation(FightOperationType.SPELL, it.target.cellId, it.spell.keys)
        }
        selectBestSpell(fightBoard, playerFighter.cell, enemies, playerAp, usesStore, cdBySpellKey)?.let {
            useSpell(fightBoard, it.spell, usesStore, cdBySpellKey, it.target)
            return FightOperation(FightOperationType.SPELL, it.target.cellId, it.spell.keys)
        }
        if (aiComplement.mustUseAllMP(playerFighter)) {
            selectBestMoveDest(playerAp, playerPosition, accessibleCells, -Short.MAX_VALUE.toInt())
                ?.takeIf { it.target.cellId != playerFighter.cell.cellId }
                ?.let { return FightOperation(FightOperationType.MOVE, it.target.cellId) }
        }
        return null
    }

    private fun useSpell(
        fightBoard: FightBoard,
        spellCombination: SpellCombination,
        usesStore: UsesStore,
        cdBySpellKey: HashMap<String, Int>,
        targetCell: DofusCell
    ) {
        val target = fightBoard.getFighter(targetCell)
        if (target != null) {
            val usesThisTurn = usesStore.computeIfAbsent(spellCombination.keys) { HashMap() }
            val usesOnThisTarget = usesThisTurn.computeIfAbsent(target.id) { 0 }
            usesThisTurn[target.id] = usesOnThisTarget + 1
        }
        cdBySpellKey[spellCombination.keys] = spellCombination.cooldown
    }

    private fun selectBestGapCloser(playerAp: Int): SpellUsage? {
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val enemies = getEnemyPlayers(fightBoard)
        val playerPositionScore = evaluateMove(playerPosition, enemies, playerPosition to 0, playerAp)
        return gapCloserCombinations
            .filter { canCastSpell(it, usesStore, cdBySpellKey, playerAp) }
            .mapNotNull { gapCloser ->
                val minRange = gapCloser.minRange
                val maxRange = getSpellMaxRange(gapCloser)
                val cellsAtRange = dofusBoard.cellsAtRange(minRange, maxRange, playerPosition)
                    .filter { it.first.isAccessible() && isGapCloserTargetValid(gapCloser, it.first, it.second) }
                    .map { it.first to 0 }
                val targetCellByRealCell = cellsAtRange.associate {
                    getRealGapCloserDest(gapCloser, playerPosition, it.first) to it.first
                }
                val realCells = targetCellByRealCell.keys.map { it to 0 }
                val moveAction = selectBestCell(playerPosition, realCells, playerAp - gapCloser.apCost) { it + 30 }
                targetCellByRealCell[moveAction.target]?.let {
                    SpellUsage(gapCloser, it, moveAction.value) to moveAction.target
                }
            }.maxByOrNull { it.first.value }
            ?.takeIf { playerPositionScore < it.first.value }
            ?.takeIf { it.second.cellId != playerPosition.cellId }
            ?.first
    }

    private fun getRealGapCloserDest(spell: SpellCombination, fromCell: DofusCell, toCell: DofusCell): DofusCell {
        if (!spell.dashToward) {
            return toCell
        }
        val dCol = toCell.col - fromCell.col
        val dRow = toCell.row - fromCell.row
        val sDCol = dCol.sign
        val sDRow = dRow.sign
        val absDCol = abs(dCol)
        val absDRow = abs(dRow)
        var destCell = fromCell
        for (i in 0 until spell.dashLength) {
            if (destCell.cellId == toCell.cellId) {
                break
            }
            val newDestCell = if (absDCol > absDRow) {
                dofusBoard.getCell(destCell.col + sDCol, destCell.row)
            } else if (absDCol < absDRow) {
                dofusBoard.getCell(destCell.col, destCell.row + sDRow)
            } else {
                val alignedCell1 =
                    dofusBoard.getCell(destCell.col + sDCol, destCell.row)
                val alignedCell2 =
                    dofusBoard.getCell(destCell.col, destCell.row + sDRow)
                if (alignedCell1 != null && alignedCell2 != null
                    && alignedCell1.isAccessible() && alignedCell2.isAccessible()
                    && !fightBoard.isFighterHere(alignedCell1) && !fightBoard.isFighterHere(alignedCell2)
                ) {
                    dofusBoard.getCell(destCell.col + sDCol, destCell.row + sDRow)
                } else {
                    null
                }
            }
            destCell = newDestCell?.takeIf { it.isAccessible() && !fightBoard.isFighterHere(it) }
                ?.takeIf { spell.canReachCell || it.cellId != toCell.cellId }
                ?: break
        }
        return destCell
    }

    private fun isGapCloserTargetValid(spell: SpellCombination, target: DofusCell, dist: Int): Boolean {
        if (target.cellId == playerFighter.cell.cellId) {
            return false
        }
        val targetFighter = fightBoard.getFighter(target.cellId)
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val los = fightBoard.lineOfSight(playerPosition, target)
        val onSameLine = dofusBoard.isOnSameLine(playerPosition.cellId, target.cellId)
        val onSameDiagonal = dofusBoard.isOnSameDiagonal(playerPosition.cellId, target.cellId)
        return if (spell.canHit && targetFighter != null) {
            canCastSpellOnTarget(spell, dist, los, onSameLine, onSameDiagonal, targetFighter, usesStore)
        } else if (targetFighter == null && spell.canTargetEmpty) {
            canCastSpell(spell, dist, los, onSameLine, onSameDiagonal)
        } else {
            false
        }
    }

    private fun selectBestMoveDest(
        playerAp: Int,
        playerPosition: DofusCell,
        accessibleCellsWithMpUsed: List<Pair<DofusCell, Int>>,
        stayOnCellScoreModifier: Int = 0,
    ): MoveAction? {
        if (getNeighborEnemies(fightBoard, playerFighter.cell).isNotEmpty()) {
            return null
        }
        return selectBestCell(playerPosition, accessibleCellsWithMpUsed, playerAp) { it + stayOnCellScoreModifier }
    }

    private fun selectBestMpBuff(
        playerAp: Int,
        alreadyExploredCells: List<DofusCell>,
        bestScoreWithoutBuff: Int
    ): SpellUsage? {
        var actionPoints = playerAp
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val exploredCells = ArrayList(alreadyExploredCells)
        for (mpBuffCombination in mpBuffCombinations.sortedBy { it.amount }) {
            val canCastSpell = canCastSpell(mpBuffCombination, usesStore, cdBySpellKey, actionPoints)
                    && canCastSpellOnTarget(mpBuffCombination, 0, true, true, true, playerFighter, usesStore)

            if (canCastSpell) {
                actionPoints -= mpBuffCombination.apCost
                val accessibleCells = fightBoard.getMoveCellsWithMpUsed(mpBuffCombination.amount, exploredCells)
                exploredCells.addAll(accessibleCells.map { it.first })
                val moveCell = selectBestCell(playerPosition, accessibleCells, actionPoints) {
                    bestScoreWithoutBuff + 500
                }
                if (moveCell.target.cellId != playerPosition.cellId) {
                    return SpellUsage(mpBuffCombination, playerPosition, 0)
                }
            }
        }
        return null
    }

    private fun selectBestCell(
        playerPosition: DofusCell,
        accessibleCellsWithDist: List<Pair<DofusCell, Int>>,
        playerAp: Int,
        stayOnCellScoreModifier: (Int) -> Int
    ): MoveAction {
        var chosenCell = playerPosition
        val enemies = getEnemyPlayers(fightBoard)
        var best = stayOnCellScoreModifier(evaluateMove(playerPosition, enemies, playerPosition to 0, playerAp))

        if (aiComplement.canMove(playerFighter)) {
            for (move in accessibleCellsWithDist) {
                val moveScore = evaluateMove(playerPosition, enemies, move, playerAp)
                if (moveScore > best) {
                    chosenCell = move.first
                    best = moveScore
                }
            }
        }
        return MoveAction(chosenCell, best)
    }

    private fun evaluateMove(
        initialPlayerPosition: DofusCell,
        enemies: List<Fighter>,
        moveDone: Pair<DofusCell, Int>,
        playerAp: Int
    ): Int {
        val state = FightState(0, 0, fightBoard.clone(), moveDone.first)
        playPlayerMove(
            state, initialPlayerPosition, moveDone, enemies, playerAp,
            usesStore.deepCopy(), HashMap(cdBySpellKey), 100
        )
        return minValue(state, initialDepth)
    }

    private fun playPlayerMove(
        state: FightState,
        initialPlayerPosition: DofusCell,
        move: Pair<DofusCell, Int>,
        enemies: List<Fighter>,
        playerAp: Int,
        usesStore: UsesStore = UsesStore(),
        cdBySpellKey: HashMap<String, Int> = HashMap(),
        aiWeightMultiplier: Int = 1
    ) {
        state.fb.move(initialPlayerPosition.cellId, move.first.cellId)
        state.playerPosition = move.first
        state.mpUsed += move.second
        var ap = playerAp
        while (true) {
            selectBestSpell(state.fb, move.first, enemies, ap, usesStore, cdBySpellKey)?.let {
                useSpell(state.fb, it.spell, usesStore, cdBySpellKey, it.target)
                state.attacksDone += it.value * aiWeightMultiplier
                ap -= it.spell.apCost
            } ?: break
        }
    }

    private fun selectBestSpell(
        fightBoard: FightBoard,
        playerPosition: DofusCell,
        enemies: List<Fighter>,
        ap: Int,
        usesStore: UsesStore,
        cdBySpellKey: Map<String, Int>
    ): SpellUsage? {
        val spells = ArrayList<SpellUsage>()
        val usableSpells = attackSpellCombinations.filter { canCastSpell(it, usesStore, cdBySpellKey, ap) }
        for (enemy in enemies) {
            val enemyPosition = enemy.cell
            val dist = dofusBoard.getDist(playerPosition, enemyPosition)
            val los = fightBoard.lineOfSight(playerPosition, enemyPosition)
            val onSameLine = dofusBoard.isOnSameLine(playerPosition.cellId, enemyPosition.cellId)
            val onSameDiagonal = dofusBoard.isOnSameDiagonal(playerPosition.cellId, enemyPosition.cellId)
            val spellsOnEnemy = usableSpells
                .filter { canCastSpellOnTarget(it, dist, los, onSameLine, onSameDiagonal, enemy, usesStore) }
                .map { SpellUsage(it, enemy.cell, getHitValue(enemies, it, playerPosition, enemy.cell)) }
            spells.addAll(spellsOnEnemy)
        }
        return spells.maxByOrNull { it.value }
    }

    private fun getHitValue(
        enemies: List<Fighter>,
        spell: SpellCombination,
        playerPosition: DofusCell,
        targetCell: DofusCell
    ): Int {
        val spellCells = spell.areaType.getAreaCells(dofusBoard, playerPosition, targetCell, spell.areaSize)
        return enemies.filter { spellCells.contains(it.cell) }
            .sumOf { (if (it.isSummon) 1 else 10).toInt() } * spell.aiWeight
    }

    private fun canCastSpellOnTarget(
        spell: SpellCombination,
        dist: Int,
        los: Boolean,
        onSameLine: Boolean,
        onSameDiagonal: Boolean,
        target: Fighter,
        usesStore: UsesStore,
    ): Boolean {
        val usesThisTurnOnTarget = usesStore[spell.keys]?.get(target.id) ?: 0
        return usesThisTurnOnTarget < spell.usesPerTurnPerTarget
                && canCastSpell(spell, dist, los, onSameLine, onSameDiagonal)
    }

    private fun canCastSpell(
        spell: SpellCombination,
        dist: Int,
        los: Boolean,
        onSameLine: Boolean,
        onSameDiagonal: Boolean,
    ): Boolean {
        return spell.keys.isNotEmpty()
                && (spell.type != SpellType.ATTACK || aiComplement.canAttack(playerFighter))
                && (!spell.castInLine && !spell.castInDiagonal
                || spell.castInLine && onSameLine
                || spell.castInDiagonal && onSameDiagonal)
                && (!spell.needsLos || los)
                && dist in spell.minRange..getSpellMaxRange(spell)
    }

    private fun canCastSpell(
        spell: SpellCombination,
        usesStore: UsesStore,
        cdBySpellKey: Map<String, Int>,
        playerAp: Int
    ): Boolean {
        val usesThisTurn = usesStore[spell.keys]?.values?.sum() ?: 0
        return usesThisTurn < spell.usesPerTurn
                && cdBySpellKey[spell.keys]?.takeIf { it > 0 } == null
                && playerAp >= spell.apCost
    }

    private fun getSpellMaxRange(spellCombination: SpellCombination): Int {
        return if (spellCombination.modifiableRange) {
            spellCombination.maxRange + playerRange
        } else spellCombination.maxRange
    }

    private fun playEnemyMove(state: FightState, enemy: Fighter, targetCell: DofusCell) {
        state.fb.move(enemy, targetCell)
    }

    private fun maxValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            val closestEnemy = state.fb.getClosestEnemy() ?: return Short.MAX_VALUE.toInt()
            return evaluateState(state, closestEnemy)
        }

        val playerMp = mpByFighterId[playerFighter.id] ?: error("Can't find MP for player")
        val accessibleCellsWithMpUsed = state.fb.getMoveCellsWithMpUsed(playerMp, state.playerPosition)
        val enemies = getEnemyPlayers(state.fb)

        var v = Int.MIN_VALUE
        for (move in accessibleCellsWithMpUsed) {
            val newState = state.clone()
            playPlayerMove(newState, state.playerPosition, move, enemies, initialPlayerAp)
            v = maxOf(v, minValue(newState, depth - 1))
        }
        return v
    }

    private fun minValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            val closestEnemy = state.fb.getClosestEnemy() ?: return Short.MAX_VALUE.toInt()
            return evaluateState(state, closestEnemy)
        }

        var v = Int.MAX_VALUE
        for (enemy in getEnemyPlayers(state.fb).filter { !it.isSummon }) {
            val movePoints = mpByFighterId[enemy.id] ?: error("MP not found for enemy : ${enemy.id}")
            val accessibleCellsWithMpUsed = state.fb.getMoveCellsWithMpUsed(movePoints, enemy.cell)
            for (move in accessibleCellsWithMpUsed) {
                val newState = state.clone()
                playEnemyMove(newState, enemy, move.first)
                v = minOf(v, maxValue(newState, depth - 1))
            }
        }
        return v
    }

    private fun evaluateState(state: FightState, closestEnemy: Fighter): Int {
        val dist = dofusBoard.getPathLength(state.playerPosition, closestEnemy.cell)
            ?: Short.MAX_VALUE.toInt()
        val idealDist = aiComplement.getIdealDistance(playerFighter, attackSpellCombinations, playerRange)
        var score = state.attacksDone
        val shouldAvoidUsingMp = aiComplement.shouldAvoidUsingMp()
        if (shouldAvoidUsingMp) {
            score -= state.mpUsed
        }
        if (!shouldAvoidUsingMp || state.attacksDone < 100) {
            score -= 10 * abs(dist - idealDist)
        }
        return score
    }

    private fun getEnemyPlayers(fightBoard: FightBoard): List<Fighter> {
        return fightBoard.getEnemyFighters()
    }

    private fun getNeighborEnemies(fightBoard: FightBoard, playerPosition: DofusCell): List<Fighter> {
        val enemies = getEnemyPlayers(fightBoard)
        return enemies.filter { playerPosition.neighbors.contains(it.cell) }
    }

    private class MoveAction(val target: DofusCell, val value: Int)

    private class SpellUsage(val spell: SpellCombination, val target: DofusCell, val value: Int)

    private class FightState(
        var attacksDone: Int,
        var mpUsed: Int,
        val fb: FightBoard,
        var playerPosition: DofusCell
    ) {
        fun clone(): FightState {
            return FightState(attacksDone, mpUsed, fb.clone(), playerPosition)
        }
    }

}