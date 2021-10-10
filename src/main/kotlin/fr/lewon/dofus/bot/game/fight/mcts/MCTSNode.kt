package fr.lewon.dofus.bot.game.fight.mcts

class MCTSNode(
    val state: MCTSState,
    val parent: MCTSNode? = null,
    val children: ArrayList<MCTSNode> = ArrayList(),
    var winScore: Double = 0.0,
    var visitCount: Int = 0,
    var childVisitCount: Int = 0,
) {

    fun clone(): MCTSNode {
        return MCTSNode(state, parent, ArrayList(children.map { it.clone() }))
    }

}