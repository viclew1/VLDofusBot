package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.operations.CooldownState
import fr.lewon.dofus.bot.game.fight.operations.FightOperation
import fr.lewon.dofus.bot.game.fight.operations.FightOperationType
import fr.lewon.dofus.bot.gui.overlay.LOSHelper
import kotlin.math.abs
import kotlin.random.Random


class FightAI(private val dofusBoard: DofusBoard, private val aiComplement: AIComplement) {

    private val cooldownState = CooldownState()

    fun onFightStart(fightBoard: FightBoard) {
        val playerFighter = fightBoard.getPlayerFighter() ?: error("Couldn't find player fighter")
        val cooldownSpellStore = cooldownState.globalCooldownSpellStore.getCooldownSpellStore(playerFighter.id)
        playerFighter.spells.filter { it.initialCooldown > 0 }.forEach {
            cooldownSpellStore[it] = it.initialCooldown + 1
        }
    }

    fun selectStartCell(fightBoard: FightBoard): DofusCell? {
        val tempFightBoard = fightBoard.deepCopy()
        val playerFighter = tempFightBoard.getPlayerFighter() ?: error("Couldn't find player fighter")
        val range = DofusCharacteristics.RANGE.getValue(playerFighter)
        val spells = playerFighter.spells
        val idealDist = aiComplement.getIdealDistance(playerFighter, spells, range)
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
        cooldownState.globalTurnUseSpellStore.clear()
        val toRemove = ArrayList<DofusSpellLevel>()
        for (cooldownSpellStore in cooldownState.globalCooldownSpellStore.values) {
            cooldownSpellStore.entries.forEach {
                val newValue = it.value - 1
                if (newValue > 0) {
                    cooldownSpellStore[it.key] = newValue
                } else {
                    toRemove.add(it.key)
                }
            }
            toRemove.forEach { cooldownSpellStore.remove(it) }
        }
    }

    fun getNextOperation(fightBoard: FightBoard, lastOperation: FightOperation?): FightOperation {
        fightBoard.getPlayerFighter() ?: error("No player fighter found")
        val dangerMap = aiComplement.buildDangerMap(dofusBoard, fightBoard.deepCopy())
        val initialState = FightState(
            fightBoard.deepCopy(), cooldownState, aiComplement, dofusBoard, dangerMap, lastOperation = lastOperation
        )
        LOSHelper.updateDangerMap(dangerMap)
        val initialNode = Node(initialState, ArrayList(), initialState.evaluate())

        var bestNode = initialNode
        val frontier = mutableListOf(initialNode)
        val startTime = System.currentTimeMillis()

        while (frontier.isNotEmpty() && System.currentTimeMillis() - startTime < 1500) {
            val nodesToExplore = if (frontier.size < 100) {
                frontier.toList()
            } else {
                selectNodesToExplore(frontier, 200)
            }
            for (node in nodesToExplore) {
                frontier.remove(node)
                if (System.currentTimeMillis() - startTime > 1500) {
                    return selectOperation(bestNode).also {
                        initialState.makeMove(it)
                    }
                }
                for (move in node.state.getPossibleOperations()) {
                    val childState = node.state.deepCopy()
                    childState.makeMove(move)
                    val operations = ArrayList(node.operations).also { it.add(move) }
                    val isPassTurn = move.type == FightOperationType.PASS_TURN
                    val childNodeScore = if (isPassTurn) node.score else childState.evaluate()
                    val childNode = Node(childState, operations, childNodeScore)
                    if (!isPassTurn) {
                        frontier.add(childNode)
                    }
                    if (bestNode.score < childNodeScore) {
                        bestNode = childNode
                        if (bestNode.score == Int.MAX_VALUE.toDouble()) {
                            return selectOperation(bestNode).also {
                                initialState.makeMove(it)
                            }
                        }
                    }
                }
            }
        }
        return selectOperation(bestNode).also {
            initialState.makeMove(it)
        }
    }

    private fun selectOperation(node: Node): FightOperation {
        return node.operations.firstOrNull()
            ?: FightOperation(FightOperationType.PASS_TURN)
    }

    private fun selectNodesToExplore(frontierNodes: List<Node>, newPopulationSize: Int): List<Node> {
        val minScore = frontierNodes.minOfOrNull { it.score }
            ?: return emptyList()
        val relativeFitnessByIndividual = frontierNodes.associateWith { getAdaptedScore(it.score, minScore - 3000) }
        val fitnessSum = relativeFitnessByIndividual.values.sum()
        val nodesToExplore = ArrayList<Node>()
        for (i in 0 until newPopulationSize) {
            val node = selectIndividualRoulette(relativeFitnessByIndividual, fitnessSum)
            if (!nodesToExplore.contains(node)) {
                nodesToExplore.add(node)
            }
        }
        return nodesToExplore
    }

    private fun selectIndividualRoulette(
        adaptedScoreByNode: Map<Node, Double>,
        fitnessSum: Double
    ): Node {
        var partialFitnessSum = 0.0
        val randomDouble = Random.nextDouble() * fitnessSum
        val entries = ArrayList<Map.Entry<Node, Double>>(adaptedScoreByNode.entries)
        for (i in adaptedScoreByNode.entries.indices.reversed()) {
            partialFitnessSum += entries[i].value
            if (partialFitnessSum >= randomDouble) {
                return entries[i].key
            }
        }
        error("Failed to select a node")
    }

    private fun getAdaptedScore(score: Double, minScore: Double): Double {
        return score - minScore
    }

    private class Node(val state: FightState, val operations: List<FightOperation>, val score: Double)
}