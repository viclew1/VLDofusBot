package fr.lewon.dofus.bot.game.fight.ai.mcts;

class BoardNodePair {
    private MctsBoard b;
    private Node p;

    public BoardNodePair(MctsBoard _b, Node _n) {
        b = _b;
        p = _n;
    }

    public MctsBoard getBoard() {
        return b;
    }

    public Node getNode() {
        return p;
    }

}
