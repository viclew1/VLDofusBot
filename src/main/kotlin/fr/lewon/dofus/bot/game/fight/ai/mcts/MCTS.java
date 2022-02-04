package fr.lewon.dofus.bot.game.fight.ai.mcts;

import fr.lewon.dofus.bot.game.fight.ai.mcts.support.HeuristicFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MCTS {
    public static final int GAMES_SIMULATED_BY_HEURISTIC = 30;
    private final Random random;
    private double explorationConstant = Math.sqrt(2.0);
    private HeuristicFunction heuristic;

    public MCTS() {
        random = new Random();
    }

    public MctsMove runAndGetBestNode(MctsBoard startingBoard, long maxTime) {
        Node rootNode = new Node(startingBoard);
        run(startingBoard, maxTime, rootNode);
        if (rootNode.endScore != null) {
            System.out.println("Perfect play results in scores " + Arrays.toString(rootNode.endScore));
        }
        Node bestNodeFound = rootNode.endScore == null ? robustChild(rootNode) : unprunedChild(rootNode);
        MctsMove bestMoveFound = bestNodeFound.move;

        Node tempBest = bestNodeFound;
        while (tempBest.children != null && !tempBest.children.isEmpty()) {
            tempBest = robustChild(tempBest);
        }
        return bestMoveFound;
    }

    public Node run(MctsBoard startingBoard, long maxTime, Node rootNode) {
        long startTime = System.currentTimeMillis();
        select(startingBoard.duplicate(), rootNode);
        if (rootNode.children.size() > 1) {
            while (shouldContinue(rootNode, maxTime, startTime)) {
                select(startingBoard.duplicate(), rootNode);
            }
        }
        return rootNode;
    }

    private boolean shouldContinue(Node rootNode, long maxTime, long startTime) {
        if (rootNode.endScore != null) {
            return false;
        } else {
            return maxTime <= 0 || getTimeSpent(startTime) <= maxTime;
        }
    }

    private long getTimeSpent(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * This represents the select stage, or default policy, of the algorithm.
     * Traverse down to the bottom of the tree using the selection strategy
     * until you find an unexpanded child node. Expand it. Run a random playout.
     * Back propagate results of the playout.
     *
     * @param currentBoard Board state to work from.
     * @param currentNode  Node from which to start selection
     */
    private void select(MctsBoard currentBoard, Node currentNode) {
        // Begin tree policy. Traverse down the tree and expand. Return
        // the new node or the deepest node it could reach. Return too
        // a board matching the returned node.
        BoardNodePair data = treePolicy(currentBoard, currentNode);
        MctsBoard b = data.getBoard();
        Node n = data.getNode();

        // If playedToEnd get score from node.endScore, else, run a random playout
        double[] score = n.endScore == null ? playout(b) : n.endScore;

        // Back propagate results of playout.
        n.backPropagateScore(score, true);
    }

    private BoardNodePair treePolicy(MctsBoard b, Node node) {
        boolean atLeaf = false;
        while (!b.gameOver() && !atLeaf && node.endScore == null) {
            atLeaf = node.children == null;
            if (atLeaf) {
                node.expandNode(b);
                if (heuristic != null) {
                    applyHeuristic(b, node);
                }
            }

            ArrayList<Node> bestNodes = findChildren(node);
            if (bestNodes.size() == 0) {
                // We have failed to find a single child to visit
                // from a non-terminal node. Maybe all nodes have been pruned,
                // or all nodes returned NaN for score, so we return a
                // random node.
                bestNodes = node.children;
            }
            node = bestNodes.get(random.nextInt(bestNodes.size()));
            b.makeMove(node.move);
        }

        if (b.gameOver()) {
            node.endScore = b.getScore();
        }

        return new BoardNodePair(b, node);
    }

    private void applyHeuristic(MctsBoard b, Node node) {
        int currentPlayer = b.getCurrentPlayer();
        if (currentPlayer < 0) {
            return;
        }
        int players = b.getQuantityOfPlayers();
        double meanWinRate = 1.0d / players;
        double meanLossRate = 1.0d - meanWinRate;
        for (Node child : node.children) {
            double h = heuristic.h(b, child.move);
            double boost = h > 0
                    ? (meanWinRate + h * meanLossRate) * GAMES_SIMULATED_BY_HEURISTIC
                    : (meanWinRate + h * meanWinRate) * GAMES_SIMULATED_BY_HEURISTIC;
            double remainder = ((double) GAMES_SIMULATED_BY_HEURISTIC - boost) / (players - 1);
            for (int i = 0; i < child.score.length; i++) {
                child.score[i] = i == currentPlayer ? boost : remainder;
                child.games = GAMES_SIMULATED_BY_HEURISTIC;
            }
        }
    }

    /**
     * Select the most visited child node
     */
    private Node robustChild(Node n) {
        double bestValue = Double.NEGATIVE_INFINITY;
        double tempBest;
        ArrayList<Node> bestNodes = new ArrayList<>();

        for (Node s : n.children) {
            tempBest = s.games;
            if (tempBest > bestValue) {
                bestNodes.clear();
                bestNodes.add(s);
                bestValue = tempBest;
            } else if (tempBest == bestValue) {
                bestNodes.add(s);
            }
        }

        return bestNodes.get(random.nextInt(bestNodes.size()));
    }

    private Node unprunedChild(Node n) {
        for (Node s : n.children) {
            if (!s.pruned) {
                return s;
            }
        }
        throw new IllegalStateException("Cannot find unpruned node for perfect play.");
    }

    /**
     * Playout function for MCTS
     */
    private double[] playout(MctsBoard board) {
        List<MctsMove> moves;
        if (board.gameOver()) {
            return board.getScore();
        }

        MctsBoard brd = board.duplicate();
        do {
            moves = brd.getMoves();
            if (moves.size() == 0) {
                throw new IllegalStateException("No legal moves for player " + brd.getCurrentPlayer());
            }
            brd.makeMove(moves.get(random.nextInt(moves.size())));
        } while (!brd.gameOver());

        return brd.getScore();
    }

    /**
     * Produce a list of viable nodes to visit. The actual selection is done in runMCTS
     */
    private ArrayList<Node> findChildren(Node n) {
        double bestValue = Double.NEGATIVE_INFINITY;
        ArrayList<Node> bestNodes = new ArrayList<>();
        boolean foundNotAtEnd = false;
        for (Node s : n.children) {
            if (!s.pruned) {
                // Only consider nodes searched to end if no other nodes have been searched to the end
                if (!foundNotAtEnd || s.endScore == null) {
                    if (!foundNotAtEnd && s.endScore == null) {
                        // Reset search now that we've found a node that was NOT searched to the end
                        foundNotAtEnd = true;
                        bestValue = Double.NEGATIVE_INFINITY;
//						bestNodes.clear(); // This is unnecessary given clear below
                    }
                    double tempBest = s.upperConfidenceBound(explorationConstant);

                    if (tempBest > bestValue) {
                        // If we found a better node
                        bestNodes.clear();
                        bestNodes.add(s);
                        bestValue = tempBest;
                    } else if (tempBest == bestValue) {
                        // If we found an equal node
                        bestNodes.add(s);
                    }
                }
            }
        }

        return bestNodes;
    }

    /**
     * Sets the exploration constant for the algorithm. You will need to find
     * the optimal value through testing. This can have a big impact on
     * performance. Default value is sqrt(2)
     */
    public void setExplorationConstant(double exp) {
        explorationConstant = exp;
    }

    public void setHeuristicFunction(HeuristicFunction h) {
        heuristic = h;
    }
}
