package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

abstract class AIComplement {

    abstract fun canAttack(playerFighter: Fighter): Boolean
    abstract fun canMove(playerFighter: Fighter): Boolean
    abstract fun mustUseAllMP(playerFighter: Fighter): Boolean
    abstract fun getIdealDistance(playerFighter: Fighter, spells: List<SpellCombination>, playerRange: Int): Int

}