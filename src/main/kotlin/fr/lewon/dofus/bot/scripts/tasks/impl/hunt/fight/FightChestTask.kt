package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.fight

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.ai.FightAI
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask

class FightChestTask : FightTask(FightChestAIComplement()) {

    override fun getFightAI(dofusBoard: DofusBoard, aiComplement: AIComplement): FightAI {
        return FightChestAI(dofusBoard, aiComplement)
    }

}