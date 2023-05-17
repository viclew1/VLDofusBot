package fr.lewon.dofus.bot.core.world

class Edge(
    val from: Vertex,
    val to: Vertex,
    val transitions: ArrayList<Transition> = ArrayList()
)