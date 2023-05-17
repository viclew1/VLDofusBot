package fr.lewon.dofus.bot.model.transition

import fr.lewon.dofus.bot.core.world.Edge
import fr.lewon.dofus.bot.core.world.Transition
import fr.lewon.dofus.bot.core.world.TransitionType

abstract class CustomTransition(edge: Edge, criterionStr: String = "") :
    Transition(edge, -1, TransitionType.UNSPECIFIED, -1, criterionStr, edge.to.mapId, -1, -1.0)