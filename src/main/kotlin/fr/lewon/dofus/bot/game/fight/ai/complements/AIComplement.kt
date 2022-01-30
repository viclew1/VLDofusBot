package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

abstract class AIComplement {

    abstract fun canAttack(playerFighter: Fighter): Boolean
    abstract fun canMove(playerFighter: Fighter): Boolean
    abstract fun mustUseAllMP(playerFighter: Fighter): Boolean
    abstract fun getIdealDistance(playerFighter: Fighter, spells: List<DofusSpellLevel>, playerRange: Int): Int
    abstract fun getIdealDistanceOLD(playerFighter: Fighter, spells: List<SpellCombination>, playerRange: Int): Int
    abstract fun shouldAvoidUsingMp(): Boolean

}