package fr.lewon.dofus.bot.model.transition

import fr.lewon.dofus.bot.core.world.Edge

class NpcTransition(edge: Edge, val npcId: Int, val npcTalkIds: List<Int>, criterionStr: String = "") :
    CustomTransition(edge, criterionStr)