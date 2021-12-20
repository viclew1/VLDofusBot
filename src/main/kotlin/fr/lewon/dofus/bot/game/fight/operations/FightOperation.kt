package fr.lewon.dofus.bot.game.fight.operations

data class FightOperation(val type: FightOperationType, val targetCellId: Int, val keys: String = "")