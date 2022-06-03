package fr.lewon.dofus.bot.scripts.tasks.impl.arena

import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.ai.FightAI
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement
import fr.lewon.dofus.bot.game.fight.ai.complements.DefaultAIComplement
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ArenaFightTask(
    aiComplement: AIComplement = DefaultAIComplement(),
    teamFight: Boolean = false
) : FightTask(aiComplement, teamFight) {

    override fun selectInitialPosition(gameInfo: GameInfo, fightBoard: FightBoard, ai: FightAI) {
        // Nothing
    }
}