package fr.lewon.dofus.bot.core.world

open class Transition(
    var edge: Edge,
    var direction: Int,
    var type: TransitionType,
    var skillId: Int,
    var criterion: String,
    var transitionMapId: Double,
    var cellId: Int,
    var id: Double
)