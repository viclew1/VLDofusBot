package fr.lewon.dofus.bot.game.fight.mcts

import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt


class MCTSAi(
    private val player: Fighter,
    private val enemy: Fighter,
    private val maxIterations: Int,
    spells: List<SpellCombination>
) {
    private val mpBuffSpells: List<SpellCombination> = spells.filter { it.type == SpellType.MP_BUFF }
    private val attackSpells: List<SpellCombination> = spells.filter { it.type == SpellType.ATTACK }
    private val gapCloserSpells: List<SpellCombination> = spells.filter { it.type == SpellType.GAP_CLOSER }

    private var opponentId = enemy.id

    fun findNextMove(board: FightBoard, fighterId: Double): FightBoard {
        val end = 2000 //TODO define end
        opponentId = getOpponentId(fighterId)
        val rootNode = MCTSNode(MCTSState(board, opponentId))
        if (getWinnerId(rootNode.state.fightBoard) != null) {
            error("Game is over, impossible to get a next move")
        }
        expand(rootNode)
        val start = System.currentTimeMillis()
        var iterations = 0
        var node = rootNode
        while (iterations++ < maxIterations && System.currentTimeMillis() - start < end) {
            val promisingNode = selection(node)
            expand(promisingNode)
            var nodeToExplore: MCTSNode = promisingNode
            if (promisingNode.children.isNotEmpty()) {
                nodeToExplore = promisingNode.children.random()
            }
            val playoutResult = simulateRandomPlay(nodeToExplore)
            backPropagation(nodeToExplore, playoutResult)
        }
        val winnerNode = rootNode.children.maxByOrNull { it.visitCount }
            ?: error("Didn't find a winner")
        return winnerNode.state.fightBoard
    }

    private fun getWinnerId(fightBoard: FightBoard): Double? {
        return when {
            fightBoard.getEnemyFighters().isEmpty() -> player.id
            fightBoard.getAlliedFighters().isEmpty() -> enemy.id
            else -> null
        }
    }

    private fun selection(node: MCTSNode): MCTSNode {
        return node.children.maxByOrNull { ucbValue(it) } ?: node
    }

    private fun ucbValue(node: MCTSNode): Double {
        return node.winScore + 2 * sqrt(
            ln(node.visitCount.toDouble() + 10.0.pow(-6)) / (node.childVisitCount + 10.0.pow(-10))
        )
    }

    private fun getAllPossibleStates(fromState: MCTSState): List<MCTSState> {
        val fightBoard = fromState.fightBoard
        val fighter = fightBoard.getFighterById(fromState.fighterId)
            ?: error("No fighter with id : $${fromState.fighterId}")
        val mp = DofusCharacteristics.MOVEMENT_POINTS.getValue(fighter)
        return fromState.fightBoard.getMoveCellsWithMpUsed(mp, fighter.cell).map { moveCell ->
            fromState.clone().also {
                it.fighterId = getOpponentId(fromState.fighterId)
                it.fightBoard.move(it.fighterId, moveCell.first.cellId)
            }
        }
    }

    private fun getOpponentId(fighterId: Double): Double {
        return if (fighterId == player.id) enemy.id else player.id
    }

    private fun expand(node: MCTSNode): MCTSNode {
        val possibleStates = getAllPossibleStates(node.state)
        possibleStates.forEach {
            val newNode = MCTSNode(it, node)
            node.children.add(newNode)
        }
        return node
    }

    private fun backPropagation(nodeToExplore: MCTSNode, fighterId: Double) {
        var tempNode: MCTSNode? = nodeToExplore
        while (tempNode != null) {
            tempNode.visitCount += 1
            if (tempNode.state.fighterId == fighterId) {
                tempNode.winScore += 1000.0 //TODO define win score
            }
            tempNode = tempNode.parent
        }
    }

    private fun simulateRandomPlay(node: MCTSNode): Double {
        val tempNode = node.clone()
        val tempState = tempNode.state
        var winnerId = getWinnerId(tempState.fightBoard)
        if (winnerId == opponentId) {
            tempNode.parent?.winScore = -1000000.0
            return opponentId
        }
        while (winnerId == null) {
            tempState.fighterId = getOpponentId(tempState.fighterId)
            tempState.randomPlay()
            winnerId = getWinnerId(tempState.fightBoard)
        }
        return winnerId
    }

}