package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.game.fight.operations.UsesStore
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import kotlin.math.abs

class FightAI(
    private val dofusBoard: DofusBoard,
    private val fightBoard: FightBoard,
    private val playerFighter: Fighter,
    private val playerRange: Int,
    private val initialDepth: Int,
    spells: List<SpellCombination>,
    private val aiComplement: AIComplement
) {

    private val mpBuffCombination = spells.firstOrNull { it.type == SpellType.MP_BUFF }
        ?: SpellCombination(SpellType.MP_BUFF)
    private val attackSpellCombinations = spells.filter { it.type == SpellType.ATTACK }
        .sortedByDescending { it.aiWeight }
    private val gapCloserCombination = spells.firstOrNull { it.type == SpellType.GAP_CLOSER }
        ?: SpellCombination(SpellType.GAP_CLOSER)

    private val usesStore = UsesStore()
    private val cdBySpellKey = HashMap<String, Int>()
    private val mpByFighterId = HashMap<Double, Int>()
    private var initialPlayerAp = 0

    fun selectStartCell(): DofusCell? {
        val tempFightBoard = fightBoard.clone()
        val idealDist = aiComplement.getIdealDistance(playerFighter, attackSpellCombinations, playerRange)
        return dofusBoard.startCells.map {
            tempFightBoard.move(playerFighter, it)
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
        playerFighter.maxHp = FighterCharacteristic.HP.getFighterCharacteristicValue(playerFighter) +
                FighterCharacteristic.VITALITY.getFighterCharacteristicValue(playerFighter)
        val playerAp = FighterCharacteristic.AP.getFighterCharacteristicValue(playerFighter)
        val playerMP = FighterCharacteristic.MP.getFighterCharacteristicValue(playerFighter)
        val enemies = getEnemyPlayers(fightBoard)

        selectBestMoveDest(playerAp, playerMP).takeIf { it.cellId != playerFighter.cell.cellId }?.let {
            return FightOperation(FightOperationType.MOVE, it.cellId)
        }
        selectBestTpDest(playerAp).takeIf { it.cellId != playerFighter.cell.cellId }?.let {
            return FightOperation(FightOperationType.SPELL, it.cellId, gapCloserCombination.keys)
        }
        getBestSpell(fightBoard, playerFighter.cell, enemies, playerAp, usesStore, cdBySpellKey)?.let {
            useSpell(fightBoard, it.spell, usesStore, cdBySpellKey, it.target.cell)
            return FightOperation(FightOperationType.SPELL, it.target.cell.cellId, it.spell.keys)
        }
        if (shouldUseMpBuff(playerAp, playerMP)) {
            useSpell(fightBoard, mpBuffCombination, usesStore, cdBySpellKey, playerFighter.cell)
            return FightOperation(FightOperationType.SPELL, playerFighter.cell.cellId, mpBuffCombination.keys)
        }
        if (aiComplement.mustUseAllMP(playerFighter)) {
            selectBestMoveDest(playerAp, playerMP).takeIf { it.cellId != playerFighter.cell.cellId }?.let {
                return FightOperation(FightOperationType.MOVE, it.cellId)
            }
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
        val target = fightBoard.getFighter(targetCell) ?: error("No fighter on cell : ${targetCell.cellId}")
        val usesThisTurn = usesStore.computeIfAbsent(spellCombination.keys) { HashMap() }
        val usesOnThisTarget = usesThisTurn.computeIfAbsent(target.id) { 0 }
        usesThisTurn[target.id] = usesOnThisTarget + 1
        cdBySpellKey[spellCombination.keys] = spellCombination.cooldown
    }

    private fun selectBestTpDest(playerAp: Int): DofusCell {
        val minRange = gapCloserCombination.minRange
        val maxRange = getSpellMaxRange(gapCloserCombination)
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val cellsAtRange = dofusBoard.cellsAtRange(minRange, maxRange, playerPosition)
            .filter { it.first.isAccessible() && !fightBoard.isFighterHere(it.first) }
        return selectBestCell(playerPosition, cellsAtRange, playerAp)
    }

    private fun selectBestMoveDest(playerAp: Int, playerMovePoints: Int): DofusCell {
        if (getNeighborEnemies(fightBoard, playerFighter.cell).isNotEmpty()) {
            return playerFighter.cell
        }
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val accessibleCells = fightBoard.getMoveCellsWithMpUsed(playerMovePoints, playerPosition)
        return selectBestCell(playerPosition, accessibleCells, playerAp)
    }

    private fun shouldUseMpBuff(playerAp: Int, playerMovePoints: Int): Boolean {
        val currentAp = initialPlayerAp - mpBuffCombination.apCost
        val canCastSpell = canCastSpell(
            mpBuffCombination, 0, true, true, true, currentAp, playerFighter, usesStore, cdBySpellKey
        )

        if (!canCastSpell) {
            return false
        }
        val currentPlayerAp = playerAp - mpBuffCombination.apCost
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val accessibleCells = fightBoard.getMoveCellsWithMpUsed(
            playerMovePoints + mpBuffCombination.amount, playerPosition
        )
        return selectBestCell(playerPosition, accessibleCells, currentPlayerAp, 500).cellId != playerPosition.cellId
    }

    private fun selectBestCell(
        playerPosition: DofusCell,
        accessibleCellsWithDist: List<Pair<DofusCell, Int>>,
        playerAp: Int,
        stayOnCellScoreModifier: Int = 0
    ): DofusCell {
        var chosenCell = playerPosition
        val enemies = getEnemyPlayers(fightBoard)
        var best = evaluateMove(playerPosition, enemies, playerPosition to 0, playerAp) + stayOnCellScoreModifier

        if (aiComplement.canMove(playerFighter)) {
            for (move in accessibleCellsWithDist) {
                evaluateMove(playerPosition, enemies, move, playerAp).takeIf { it > best }?.let {
                    chosenCell = move.first
                    best = it
                }
            }
        }
        return chosenCell
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
        getBestSpell(state.fb, move.first, enemies, ap, usesStore, cdBySpellKey)?.let {
            useSpell(state.fb, it.spell, usesStore, cdBySpellKey, it.target.cell)
            state.attacksDone += it.value * aiWeightMultiplier
            ap -= it.spell.apCost
        }
    }

    private fun getBestSpell(
        fightBoard: FightBoard,
        playerPosition: DofusCell,
        enemies: List<Fighter>,
        ap: Int,
        usesStore: UsesStore,
        cdBySpellKey: Map<String, Int>
    ): SpellUsage? {
        val spells = ArrayList<SpellUsage>()
        for (enemy in enemies) {
            val enemyPosition = enemy.cell
            val dist = dofusBoard.getDist(playerPosition, enemyPosition)
            val los = fightBoard.lineOfSight(playerPosition, enemyPosition)
            val onSameLine = dofusBoard.isOnSameLine(playerPosition.cellId, enemyPosition.cellId)
            val onSameDiagonal = dofusBoard.isOnSameDiagonal(playerPosition.cellId, enemyPosition.cellId)
            val spellsOnEnemy = attackSpellCombinations
                .filter { canCastSpell(it, dist, los, onSameLine, onSameDiagonal, ap, enemy, usesStore, cdBySpellKey) }
                .map { SpellUsage(it, enemy, getHitValue(enemies, it, playerPosition, enemy.cell)) }
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

    private fun canCastSpell(
        spell: SpellCombination,
        dist: Int,
        los: Boolean,
        onSameLine: Boolean,
        onSameDiagonal: Boolean,
        ap: Int,
        target: Fighter,
        usesStore: UsesStore,
        cdBySpellKey: Map<String, Int>
    ): Boolean {
        val usesThisTurn = usesStore[spell.keys]?.values?.sum() ?: 0
        val usesThisTurnOnTarget = usesStore[spell.keys]?.get(target.id) ?: 0
        return spell.keys.isNotEmpty()
                && (spell.type != SpellType.ATTACK || aiComplement.canAttack(playerFighter))
                && usesThisTurn < spell.usesPerTurn
                && usesThisTurnOnTarget < spell.usesPerTurnPerTarget
                && cdBySpellKey[spell.keys]?.takeIf { it > 0 } == null
                && (!spell.castInLine && !spell.castInDiagonal
                || spell.castInLine && onSameLine
                || spell.castInDiagonal && onSameDiagonal)
                && ap >= spell.apCost
                && (!spell.needsLos || los)
                && dist in spell.minRange..getSpellMaxRange(spell)
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

    private class SpellUsage(val spell: SpellCombination, val target: Fighter, val value: Int)

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