package fr.lewon.dofus.bot.game.fight.ai.impl

import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.FightAI
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement

abstract class ArenaAI(dofusBoard: DofusBoard, aiComplement: AIComplement) : FightAI(dofusBoard, aiComplement) {

    override fun selectStartCell(fightBoard: FightBoard): DofusCell? {
        return null
    }

    protected fun getSpellByName(fighter: Fighter, name: String): DofusSpellLevel {
        return fighter.spells.firstOrNull {
            SpellManager.getSpell(it.spellId)?.name?.lowercase() == name.lowercase()
        } ?: error("Spell not found : $name")
    }
}