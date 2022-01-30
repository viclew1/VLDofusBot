package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
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

    override fun getIdealDistanceOLD(playerFighter: Fighter, spells: List<SpellCombination>, playerRange: Int): Int {
        val maxRangeFunc: (SpellCombination) -> Int = { spell ->
            if (spell.modifiableRange) {
                spell.maxRange + playerRange
            } else spell.maxRange
        }
        if (spellMaxRange == null) {
            spellMaxRange = spells.map { maxRangeFunc(it) }.maxOrNull()
        }
        return spellMaxRange ?: 0
    }

    override fun getIdealDistance(playerFighter: Fighter, spells: List<DofusSpellLevel>, playerRange: Int): Int {
        if (spellMaxRange == null) {
            spellMaxRange = getSpellMaxRange(spells, playerRange)
        }
        return spellMaxRange ?: 0
    }

    private fun getSpellMaxRange(spells: List<DofusSpellLevel>, playerRange: Int): Int? {
        return spells.map { getSpellMaxRange(it, playerRange) }.maxOrNull()
    }

    private fun getSpellMaxRange(spell: DofusSpellLevel, playerRange: Int): Int {
        return if (spell.rangeCanBeBoosted) {
            spell.maxRange + playerRange
        } else spell.maxRange
    }

    override fun shouldAvoidUsingMp(): Boolean {
        return true
    }
}