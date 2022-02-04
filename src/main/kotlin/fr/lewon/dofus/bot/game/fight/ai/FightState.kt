package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusEffectZoneType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectGlobalType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellEffectType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.ai.mcts.MctsBoard
import fr.lewon.dofus.bot.game.fight.ai.mcts.MctsMove
import fr.lewon.dofus.bot.game.fight.operations.CooldownState
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import kotlin.math.max

class FightState(
    private val fb: FightBoard,
    private val cooldownState: CooldownState,
    private val aiComplement: AIComplement,
    private val dofusBoard: DofusBoard,
    private val spellSimulator: SpellSimulator = SpellSimulator(dofusBoard),
    private val effectZoneCalculator: EffectZoneCalculator = EffectZoneCalculator(dofusBoard),
    private val playerFighter: Fighter = getPlayerFighter(fb),
    private val fighterByPlayer: Map<Int, Fighter> = buildFighterByPlayer(fb),
    private var player: Int = getStartingPlayer(fighterByPlayer, playerFighter),
    private var lastOperation: FightOperation? = null
) : MctsBoard {

    companion object {
        private fun buildFighterByPlayer(fightBoard: FightBoard): Map<Int, Fighter> {
            return fightBoard.getAllFighters(false).sortedByDescending {
                DofusCharacteristics.INITIATIVE.getValue(it)
            }.withIndex().associate { it.index to it.value }
        }

        private fun getStartingPlayer(fighterIdByPlayer: Map<Int, Fighter>, playerFighter: Fighter): Int {
            return fighterIdByPlayer.entries.indexOfFirst { it.value.id == playerFighter.id }
        }

        private fun getPlayerFighter(fb: FightBoard): Fighter {
            return fb.getPlayerFighter() ?: error("Couldn't find player fighter")
        }
    }

    override fun duplicate(): MctsBoard {
        return FightState(
            fb.deepCopy(),
            cooldownState.deepCopy(),
            aiComplement,
            dofusBoard,
            spellSimulator,
            effectZoneCalculator,
            playerFighter,
            fighterByPlayer,
            player,
            lastOperation
        )
    }

    private fun getCurrentFighter(): Fighter? {
        val fighterId = fighterByPlayer[player]?.id ?: error("Fighter not found for player : $player")
        return fb.getFighterById(fighterId)
    }

    override fun getMoves(): MutableList<out MctsMove> {
        val currentFighter = getCurrentFighter() ?: error("Couldn't find current fighter : $player")
        val currentFighterPosition = currentFighter.cell

        val cooldownState = cooldownState
        val currentFighterAp = DofusCharacteristics.ACTION_POINTS.getValue(currentFighter)
        val currentFighterMp = DofusCharacteristics.MOVEMENT_POINTS.getValue(currentFighter)

        val options = ArrayList<FightOperation>()
        options.add(FightOperation(FightOperationType.PASS_TURN))
        if (canMove(currentFighter)) {
            for (cell in fb.getMoveCellsWithMpUsed(currentFighterMp, currentFighterPosition)) {
                if (fb.getFighter(cell.first.cellId) != null) {
                    continue
                }
                val operation = FightOperation(FightOperationType.MOVE, cell.first.cellId, dist = cell.second)
                options.add(operation)
            }
        }
        val alignmentCalculatorByTarget = HashMap<Int, AlignmentCalculator>()
        val spells = currentFighter.spells
        for (spell in spells.filter { isSpellReady(currentFighter, it, cooldownState, currentFighterAp) }) {
            for (cellId in getPossibleTargetCellIds(currentFighterPosition, spell)) {
                val ac = alignmentCalculatorByTarget.computeIfAbsent(cellId) {
                    AlignmentCalculator(dofusBoard, fb, currentFighterPosition.cellId, it)
                }
                if (canCastSpellOnTarget(currentFighter, spell, ac, cellId, cooldownState)) {
                    val operation = FightOperation(FightOperationType.SPELL, cellId, spell)
                    options.add(operation)
                }
            }
        }
        return options
    }

    private fun canMove(currentFighter: Fighter): Boolean {
        return aiComplement.canMove(currentFighter)
                && lastOperation?.type != FightOperationType.MOVE
                && getNeighborEnemies(currentFighter).isEmpty()
    }

    private fun canCastSpellOnTarget(
        currentFighter: Fighter,
        spell: DofusSpellLevel,
        alignmentCalculator: AlignmentCalculator,
        targetCellId: Int,
        cooldownState: CooldownState,
    ): Boolean {
        val fighter = fb.getFighter(targetCellId)
        if (spell.needTakenCell && fighter == null || spell.needFreeCell && fighter != null) {
            return false
        }
        val turnUseSpellStore = cooldownState.globalTurnUseSpellStore.getTurnSpellUseStore(currentFighter.id)
        if (spell.maxCastPerTarget > 0) {
            fighter?.takeIf { turnUseSpellStore.getUsesOnTarget(spell, it.id) >= spell.maxCastPerTarget }
                ?.let { return false }
        }
        return canCastSpell(currentFighter, spell, alignmentCalculator)
    }

    private fun canCastSpell(
        currentFighter: Fighter,
        spell: DofusSpellLevel,
        alignmentCalculator: AlignmentCalculator,
    ): Boolean {
        return (!isSpellAttack(spell) || aiComplement.canAttack(currentFighter))
                && (!spell.castInLine && !spell.castInDiagonal
                || spell.castInLine && alignmentCalculator.onSameLine
                || spell.castInDiagonal && alignmentCalculator.onSameDiagonal)
                && alignmentCalculator.dist in spell.minRange..getSpellMaxRange(spell, currentFighter)
                && (!spell.castTestLos || alignmentCalculator.los)
    }

    private fun isSpellAttack(spell: DofusSpellLevel): Boolean {
        return spell.effects.firstOrNull { it.effectType.globalType == DofusSpellEffectGlobalType.ATTACK } != null
    }

    private fun isSpellReady(
        currentFighter: Fighter, spell: DofusSpellLevel, cooldownState: CooldownState, currentFighterAp: Int
    ): Boolean {
        val turnUseSpellStore = cooldownState.globalTurnUseSpellStore.getTurnSpellUseStore(currentFighter.id)
        val cooldownSpellStore = cooldownState.globalCooldownSpellStore.getCooldownSpellStore(currentFighter.id)
        val usesThisTurn = turnUseSpellStore.getTotalUses(spell)
        return (spell.maxCastPerTurn <= 0 || usesThisTurn < spell.maxCastPerTurn)
                && cooldownSpellStore[spell]?.takeIf { it > 0 } == null
                && currentFighterAp >= spell.apCost
    }

    private fun getSpellMaxRange(spell: DofusSpellLevel, currentFighter: Fighter): Int {
        return if (spell.rangeCanBeBoosted) {
            val range = DofusCharacteristics.RANGE.getValue(currentFighter)
            max(spell.minRange, spell.maxRange + range)
        } else spell.maxRange
    }

    private fun getNeighborEnemies(currentFighter: Fighter): List<Fighter> {
        val playerPosition = currentFighter.cell
        val enemies = fb.getAllFighters().filter { it.teamId != currentFighter.teamId }
        val neighborsIds = playerPosition.neighbors.map { it.cellId }
        return enemies.filter { neighborsIds.contains(it.cell.cellId) }
    }

    private fun getPossibleTargetCellIds(currentFighterPosition: DofusCell, spell: DofusSpellLevel): List<Int> {
        val fighterCells = fb.getAllFighters().map { it.cell.cellId }
        if (spell.needTakenCell || spell.effects.all { it.rawZone.effectZoneType == DofusEffectZoneType.POINT }) {
            return fighterCells
        }
        val effectTypes = spell.effects.map { it.effectType }
        val isMoveSpell = effectTypes.contains(DofusSpellEffectType.TELEPORT)
                || effectTypes.contains(DofusSpellEffectType.DASH)
        if (isMoveSpell) {
            return dofusBoard.cellsAtRange(spell.minRange, spell.maxRange, currentFighterPosition)
                .map { it.first.cellId }
        }
        val effectZones = spell.effects.map { it.rawZone }
        val allCells = fighterCells.flatMap {
            effectZoneCalculator.getAffectedCells(currentFighterPosition.cellId, it, effectZones)
        }
        if (spell.needFreeCell) {
            return allCells.filter { !fighterCells.contains(it) }
        }
        return allCells
    }

    override fun makeMove(m: MctsMove) {
        val operation = m as FightOperation
        val currentFighter = getCurrentFighter() ?: error("Couldn't find current fighter : $player")
        when (operation.type) {
            FightOperationType.MOVE -> {
                val targetCellId = operation.targetCellId ?: error("Target cell id mandatory")
                fb.move(currentFighter.id, targetCellId)
            }
            FightOperationType.SPELL -> {
                val spell = operation.spell ?: error("Spell can't be null for SPELL operation")
                val targetCellId = operation.targetCellId ?: error("Target cell id mandatory")
                useSpell(currentFighter, spell, targetCellId)
            }
            FightOperationType.PASS_TURN -> {
                decreaseCds(currentFighter)
                resetUsesThisTurn(currentFighter)
                setNextPlayer()
            }
        }
        fb.getAllFighters().filter { it.getCurrentHp() <= 0 }.forEach {
            fb.killFighter(it.id)
        }
        lastOperation = operation
    }

    private fun decreaseCds(currentFighter: Fighter) {
        val toRemove = ArrayList<DofusSpellLevel>()
        val cooldownSpellStore = cooldownState.globalCooldownSpellStore.getCooldownSpellStore(currentFighter.id)
        cooldownSpellStore.entries.forEach {
            val newValue = it.value - 1
            if (newValue > 0) {
                cooldownSpellStore[it.key] = newValue
            } else {
                toRemove.add(it.key)
            }
        }
        toRemove.forEach { spell -> cooldownSpellStore.remove(spell) }
    }

    private fun resetUsesThisTurn(currentFighter: Fighter) {
        cooldownState.globalTurnUseSpellStore.getTurnSpellUseStore(currentFighter.id).clear()
    }

    private fun setNextPlayer() {
        player++
        if (player == fighterByPlayer.size) {
            player = 0
        }
        if (getCurrentFighter() == null) {
            setNextPlayer()
        }
    }

    private fun useSpell(currentFighter: Fighter, spell: DofusSpellLevel, targetCellId: Int) {
        val target = fb.getFighter(targetCellId)
        val targetId = target?.id ?: Int.MAX_VALUE.toDouble()
        val turnUseSpellStore = cooldownState.globalTurnUseSpellStore.getTurnSpellUseStore(currentFighter.id)
        val cooldownSpellStore = cooldownState.globalCooldownSpellStore.getCooldownSpellStore(currentFighter.id)
        val usesThisTurn = turnUseSpellStore.computeIfAbsent(spell) { HashMap() }
        val usesOnThisTarget = usesThisTurn.computeIfAbsent(targetId) { 0 }
        usesThisTurn[targetId] = usesOnThisTarget + 1
        cooldownSpellStore[spell] = spell.minCastInterval
        spellSimulator.simulateSpell(fb, currentFighter, spell, targetCellId)
    }

    override fun gameOver(): Boolean {
        return fb.getEnemyFighters().isEmpty() || fb.getAlliedFighters().isEmpty()
    }

    override fun getCurrentPlayer(): Int {
        return player
    }

    override fun getQuantityOfPlayers(): Int {
        return fighterByPlayer.size
    }

    override fun getScore(): DoubleArray {
        val win = fb.getEnemyFighters().isEmpty()
        return Array(fighterByPlayer.size) {
            val isAlly = fighterByPlayer[it]?.teamId == playerFighter.teamId
            if (win && isAlly || !win && !isAlly) 1.0 else 0.0
        }.toDoubleArray()
    }

    fun evaluate(dangerByCell: Map<Int, Int>, operations: List<MctsMove>): Double {
        val allies = fb.getAlliedFighters()
        val realAlliesCount = allies.filter { !it.isSummon }.size
        if (realAlliesCount == 0) {
            return Int.MIN_VALUE.toDouble()
        }
        val enemies = fb.getEnemyFighters()
        val realEnemiesCount = enemies.filter { !it.isSummon }.size
        if (realEnemiesCount == 0) {
            return Int.MAX_VALUE.toDouble()
        }
        val alliesHp = allies.sumOf {
            it.getCurrentHp() * (if (it.isSummon) 1 else 10)
        }
        val enemiesHp = enemies.sumOf {
            it.getCurrentHp() * (if (it.isSummon) 1 else 10)
        }
        val danger = allies.filter { !it.isSummon }
            .sumOf { dangerByCell[it.cell.cellId] ?: 0 }
        return (realAlliesCount * 2500
                - realEnemiesCount * 10000
                + alliesHp * 5
                - enemiesHp
                - danger
                - operations.size).toDouble()
    }
}
