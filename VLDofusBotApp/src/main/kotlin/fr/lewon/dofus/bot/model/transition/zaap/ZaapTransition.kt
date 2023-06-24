package fr.lewon.dofus.bot.model.transition.zaap

import fr.lewon.dofus.bot.core.world.Edge
import fr.lewon.dofus.bot.model.transition.CustomTransition

class ZaapTransition(edge: Edge) : CustomTransition(edge, "${ZaapCriterionKey}=${edge.to.mapId}")