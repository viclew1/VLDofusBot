package fr.lewon.dofus.bot.game.fight.ai.mcts;

import java.util.List;

public interface MctsBoard {

    /**
     * Create one copy of the board. It is important that the copies do
     * not store references to objects shared by other boards unless
     * those objects are immutable.
     */
    MctsBoard duplicate();

    /**
     * Get a list of all available moves for the current state. MCTS
     * calls this to know what actions are possible at that point.
     * <p>
     * The location parameter indicates from where in the algorithm
     * the method was called. Can be either treePolicy or playout.
     */
    List<MctsMove> getMoves();

    /**
     * Apply the move m to the current state of the board.
     */
    void makeMove(MctsMove m);

    /**
     * Returns true if the game is over.
     */
    boolean gameOver();

    /**
     * Returns the player ID for the player whose turn is active. This method is
     * called by the MCTS.
     */
    int getCurrentPlayer();

    /**
     * Returns the number of players.
     */
    int getQuantityOfPlayers();

    /**
     * Returns a score vector.
     * [1.0, 0.0] indicates a win for player 0.
     * [0.0, 1.0] indicates a win for player 1
     * [0.5, 0.5] indicates a draw
     *
     * @return score array
     */
    double[] getScore();

    /**
     * Return the player that would play next if
     * the current player played the given move
     *
     * @param move A move by the current player. Games may ignore
     *             this if the order of play is pre-determined
     * @return The next player
     */
    default int getNextPlayer(MctsMove move) {
        MctsBoard tempBoard = duplicate();
        tempBoard.makeMove(move);
        return tempBoard.getCurrentPlayer();
    }
}