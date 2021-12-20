package fr.lewon.dofus.bot.game.fight.complements

import fr.lewon.dofus.bot.game.fight.AIComplement
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

class DefaultAIComplement(
    private val canAttack: Boolean = true,
    private val canMove: Boolean = true,
    private val shouldUseAllMP: Boolean = false,
) : AIComplement() {

    var spellMaxRange: Int? = null

    override fun canAttack(playerFighter: Fighter): Boolean {
        return canAttack
    }

    override fun canMove(playerFighter: Fighter): Boolean {
        return canMove
    }

    override fun mustUseAllMP(playerFighter: Fighter): Boolean {
        return shouldUseAllMP
    }

    override fun getIdealDistance(playerFighter: Fighter, spells: List<SpellCombination>, playerRange: Int): Int {
        if (spellMaxRange == null) {
            spellMaxRange = getSpellMaxRange(spells, playerRange)
        }
        return spellMaxRange ?: 0
    }

    private fun getSpellMaxRange(spells: List<SpellCombination>, playerRange: Int): Int? {
        return spells.map { getSpellMaxRange(it, playerRange) }.maxOrNull()
    }

    private fun getSpellMaxRange(spell: SpellCombination, playerRange: Int): Int {
        return if (spell.modifiableRange) {
            spell.maxRange + playerRange
        } else spell.maxRange
    }

}