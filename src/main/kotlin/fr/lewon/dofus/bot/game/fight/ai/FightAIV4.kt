package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectGlobalType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.operations.CooldownState
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.model.characters.spells.AreaType
import kotlin.math.abs
import kotlin.math.max

class FightAIV4(
    private val dofusBoard: DofusBoard,
    private val playerBaseRange: Int,
    private val spells: List<DofusSpellLevel>,
    private val aiComplement: AIComplement
) {

    private val cooldownState = CooldownState()
    private val damageCalculator = DamageCalculator()
    private val spellSimulator = SpellSimulator(dofusBoard)

    init {
        spells.filter { it.initialCooldown > 0 }.forEach {
            cooldownState.cdBySpell[it] = it.initialCooldown
        }
    }

    fun selectStartCell(fightBoard: FightBoard): DofusCell? {
        val tempFightBoard = fightBoard.deepCopy()
        val playerFighter = tempFightBoard.getPlayerFighter() ?: error("Couldn't find player fighter")
        val idealDist = aiComplement.getIdealDistance(playerFighter, spells, playerBaseRange)
        return dofusBoard.startCells.map {
            val tempPlayerFighter = tempFightBoard.getPlayerFighter()
                ?: error("Player fighter not found")
            tempFightBoard.move(tempPlayerFighter, it)
            val closestEnemy = tempFightBoard.getClosestEnemy() ?: error("Closest enemy not found")
            it to (dofusBoard.getPathLength(it, closestEnemy.cell)
                ?: dofusBoard.getDist(it, closestEnemy.cell))
        }.minByOrNull { abs(idealDist - it.second) }?.first
    }

    fun onNewTurn(fightBoard: FightBoard) {
        buildDangerCells(fightBoard)
        cooldownState.turnSpellUseStore.clear()
        val toRemove = ArrayList<DofusSpellLevel>()
        cooldownState.cdBySpell.entries.forEach {
            val newValue = it.value - 1
            if (newValue > 0) {
                cooldownState.cdBySpell[it.key] = newValue
            } else {
                toRemove.add(it.key)
            }
        }
        toRemove.forEach { cooldownState.cdBySpell.remove(it) }
    }

    private fun buildDangerCells(fightBoard: FightBoard) {
        //TODO build a map with danger by cell
    }

    fun getNextOperation(fightBoard: FightBoard): FightOperation? {
        val playerFighter = fightBoard.getPlayerFighter()
            ?: error("No player fighter found")
        damageCalculator.resetCache()
        val playerAp = DofusCharacteristics.ACTION_POINTS.getValue(playerFighter)
        val playerMP = DofusCharacteristics.MOVEMENT_POINTS.getValue(playerFighter)
        val playerRange = playerBaseRange

        val initialState = FightState(fightBoard.deepCopy(), cooldownState, ArrayList(), playerAp, playerMP)
        var frontier: List<FightState> = listOf(initialState)
        val turns = ArrayList<FightState>().also { it.add(initialState) }
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<FightState>()
            for (state in frontier) {
                val newStates = getAllPossibleStates(initialState, playerRange)
                for (newState in newStates) {
                    newFrontier.add(newState)
                    turns.add(newState)
                }
            }
            frontier = newFrontier
        }
        println("Turns count : ${turns.size}")
        return turns.maxByOrNull { evaluateState(it) }?.operations?.firstOrNull()
    }

    private fun useSpell(
        fightBoard: FightBoard,
        spell: DofusSpellLevel,
        targetCellId: Int,
        cooldownState: CooldownState
    ) {
        val target = fightBoard.getFighter(targetCellId)
        if (target != null) {
            val usesThisTurn = cooldownState.turnSpellUseStore.computeIfAbsent(spell) { HashMap() }
            val usesOnThisTarget = usesThisTurn.computeIfAbsent(target.id) { 0 }
            usesThisTurn[target.id] = usesOnThisTarget + 1
        }
        cooldownState.cdBySpell[spell] = spell.minCastInterval
        spellSimulator.simulateSpell(fightBoard, spell, targetCellId)
    }

    private fun getAllPossibleStates(initialFightState: FightState, playerRange: Int): List<FightState> {
        val fightBoard = initialFightState.fb
        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")
        val playerPosition = playerFighter.cell

        val cooldownState = initialFightState.cooldownState
        val playerAp = initialFightState.playerAp
        val playerMp = initialFightState.playerMp

        val states = ArrayList<FightState>()
        if (aiComplement.canMove(playerFighter) && getNeighborEnemies(fightBoard, playerPosition).isNotEmpty()) {
            for (cell in fightBoard.getMoveCellsWithMpUsed(playerMp, playerPosition)) {
                val newFs = initialFightState.deepCopy(playerMp - cell.second)
                newFs.operations.add(FightOperation(FightOperationType.MOVE, cell.first.cellId))
                newFs.fb.move(playerFighter.id, cell.first.cellId)
                states.add(newFs)
            }
        }
        val alignmentCalculatorByTarget = HashMap<Int, AlignmentCalculator>()
        for (spell in spells.filter { isSpellReady(it, cooldownState, playerAp) }) {
            for (target in getSpellTargets(playerPosition, spell)) {
                val ac = alignmentCalculatorByTarget.computeIfAbsent(target) {
                    AlignmentCalculator(dofusBoard, fightBoard, playerPosition.cellId, it)
                }
                if (canCastSpellOnTarget(fightBoard, playerFighter, playerRange, spell, ac, target, cooldownState)) {
                    val newFs = initialFightState.deepCopy(playerAp - spell.apCost)
                    newFs.operations.add(FightOperation(FightOperationType.SPELL, target, spell))
                    useSpell(newFs.fb, spell, target, newFs.cooldownState)
                    states.add(newFs)
                }
            }
        }
        return states
    }

    private fun getSpellTargets(playerPosition: DofusCell, spell: DofusSpellLevel): List<Int> {
        return AreaType.CIRCLE.getAreaCells(dofusBoard, playerPosition, playerPosition, spell.maxRange)
            .map { it.cellId }
    }

    private fun evaluateState(state: FightState): Int {
        return 0
    }

    private fun canCastSpellOnTarget(
        fightBoard: FightBoard,
        playerFighter: Fighter,
        playerRange: Int,
        spell: DofusSpellLevel,
        alignmentCalculator: AlignmentCalculator,
        targetCellId: Int,
        cooldownState: CooldownState,
    ): Boolean {
        val fighter = fightBoard.getFighter(targetCellId)
        if (spell.needTakenCell && fighter == null || spell.needFreeCell && fighter != null) {
            return false
        }
        if (spell.maxCastPerTarget > 0) {
            fighter?.takeIf { cooldownState.turnSpellUseStore.getUsesOnTarget(spell, it.id) >= spell.maxCastPerTarget }
                ?.let { return false }
        }
        return canCastSpell(playerFighter, playerRange, spell, alignmentCalculator)
    }

    private fun canCastSpell(
        playerFighter: Fighter,
        playerRange: Int,
        spell: DofusSpellLevel,
        alignmentCalculator: AlignmentCalculator,
    ): Boolean {
        return (!isSpellAttack(spell) || aiComplement.canAttack(playerFighter))
                && (!spell.castInLine && !spell.castInDiagonal
                || spell.castInLine && alignmentCalculator.onSameLine
                || spell.castInDiagonal && alignmentCalculator.onSameDiagonal)
                && alignmentCalculator.dist in spell.minRange..getSpellMaxRange(playerRange, spell)
                && (!spell.castTestLos || alignmentCalculator.los)
    }

    private fun isSpellAttack(spell: DofusSpellLevel): Boolean {
        return spell.effects.firstOrNull { it.effectType.globalType == DofusSpellEffectGlobalType.ATTACK } != null
    }

    private fun isSpellReady(spell: DofusSpellLevel, cooldownState: CooldownState, playerAp: Int): Boolean {
        val usesThisTurn = cooldownState.turnSpellUseStore[spell]?.values?.sum() ?: 0
        return (spell.maxCastPerTurn <= 0 || usesThisTurn < spell.maxCastPerTurn)
                && cooldownState.cdBySpell[spell]?.takeIf { it > 0 } == null
                && playerAp >= spell.apCost
    }

    private fun getSpellMaxRange(playerRange: Int, spellCombination: DofusSpellLevel): Int {
        return if (spellCombination.rangeCanBeBoosted) {
            max(spellCombination.minRange, spellCombination.maxRange + playerRange)
        } else spellCombination.maxRange
    }

    private fun getEnemyPlayers(fightBoard: FightBoard): List<Fighter> {
        return fightBoard.getEnemyFighters()
    }

    private fun getNeighborEnemies(fightBoard: FightBoard, playerPosition: DofusCell): List<Fighter> {
        val enemies = getEnemyPlayers(fightBoard)
        val neighborsIds = playerPosition.neighbors.map { it.cellId }
        return enemies.filter { neighborsIds.contains(it.cell.cellId) }
    }

}