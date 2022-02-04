package fr.lewon.dofus.bot.game.fight.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This type of node does not store the board, so that makeMove() must be called to
 * navigate the tree. An alternative node that remembers the board (at least for leaf
 * nodes, which likely constitute a majority of the nodes) does not exist.
 */
public class Node implements Comparable<Node> {
    public double[] score;
    public double games; // double outperforms int slightly
    public MctsMove move;
    public ArrayList<Node> children;
    public Node parent;
    public int player;
    public boolean pruned;
    public double[] endScore = null;

    /**
     * This creates the root node
     */
    public Node(MctsBoard b) {
        player = b.getCurrentPlayer();
        score = new double[b.getQuantityOfPlayers()];
    }

    /**
     * This creates non-root nodes
     */
    private Node(MctsBoard b, MctsMove move, Node parent) {
        this.parent = parent;
        this.move = move;
        player = b.getNextPlayer(move);
        score = new double[b.getQuantityOfPlayers()];
    }

    /**
     * Return the upper confidence bound of this state
     *
     * @param c typically sqrt(2). Increase to emphasize exploration. Decrease
     *          to increment exploitation
     */
    double upperConfidenceBound(double c) {
        double score = this.score[parent.player];
        double exploitation = safeDivision(score, games);
        double exploration = c * Math.sqrt(safeDivision(Math.log(parent.games + 1), games));
        return exploitation + exploration;
    }

    private double safeDivision(double numerator, double denominator) {
        return games == 0 ? numerator == 0 ? 1.0 : Double.POSITIVE_INFINITY : numerator / denominator;
    }

    /**
     * Update the tree with the new score.
     */
    void backPropagateScore(double[] score, boolean prune) {
        games++;
        for (int i = 0; i < score.length; i++) {
            this.score[i] += score[i];
        }

        if (prune && children != null) {
            boolean allPlayedToEnd = true;
            Node bestChild = null;
            double bestScore = -1.0;
            for (Iterator<Node> iterator = children.iterator(); bestScore != 1.0 && iterator.hasNext(); ) {
                Node child = iterator.next();
                allPlayedToEnd = allPlayedToEnd && child.endScore != null;
                if (child.endScore != null && child.endScore[player] > bestScore) {
                    bestScore = child.endScore[player];
                    bestChild = child;
                }
            }
            if (bestScore == 1.0 || allPlayedToEnd) {
                pruneAllBut(bestChild);
                for (int i = 0; i < score.length; i++) {
                    this.score[i] = score[i] * games;
                }
            } else {
                // No point pruning at parent level if no pruning done at child
                prune = false;
            }
        }

        if (parent != null) {
            parent.backPropagateScore(score, prune);
        }
    }

    /**
     * Call this when best play is known to be this node to prune all other
     * nodes and adjust scores accordingly. A win is a win.
     *
     * @param node the child that is best play for current player
     */
    private void pruneAllBut(Node node) {
        for (Node child : children) {
            if (child != node) {
                child.pruned = true;
            }
        }
        assert node.endScore != null;
        endScore = node.endScore;
    }

    /**
     * Expand this node by populating its list of unvisited child nodes.
     */
    void expandNode(MctsBoard currentBoard) {
        List<MctsMove> legalMoves = currentBoard.getMoves();
        children = new ArrayList<>();
        for (MctsMove legalMove : legalMoves) {
            children.add(new Node(currentBoard, legalMove, this));
        }
    }

    @Override
    public int compareTo(Node o) {
        return move.compareTo(o.move);
    }

    @Override
    public String toString() {
        String result;
        String[] scoreStrings = new String[score.length];
        for (int i = 0; i < score.length; i++) {
            scoreStrings[i] = String.valueOf(score[i]);
        }
        if (parent == null) {
            result = "ROOT";
        } else if (parent.player < 0) {
            result = "" + move + " " + String.join(", ", scoreStrings);
        } else {
            result = "" + move + " " + String.join(", ", scoreStrings) + " " + parent.player + " wins " + (int) (score[parent.player] * 100 / games) + "% of " + games;
        }
        if (endScore != null) {
            result = result + " end score" + Arrays.toString(endScore);
        }
        if (pruned) {
            result = result + " PRUNED";
        }
        return result;
    }

}