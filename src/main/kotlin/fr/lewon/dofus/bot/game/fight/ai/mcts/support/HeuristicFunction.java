package fr.lewon.dofus.bot.game.fight.ai.mcts.support;

import fr.lewon.dofus.bot.game.fight.ai.mcts.MctsBoard;
import fr.lewon.dofus.bot.game.fight.ai.mcts.MctsMove;

/**
 * Create a class implementing this interface and instantiate
 * it. Pass the instance to the MCTS instance using the
 * {@link fr.lewon.dofus.bot.game.fight.ai.mcts.MCTS#setHeuristicFunction(HeuristicFunction h) setHeuristicFunction} method.
 */
public interface HeuristicFunction {
    /**
     * Calculates a score for the quality of this move on this board. Should range
     * from -1 (bad) to 1 (good).
     *
     * @param board the board upon which this move could be played
     * @param move  the move to be evaluated
     * @return a score in rance [-1, 1]
     */
    double h(MctsBoard board, MctsMove move);
}
