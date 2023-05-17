package fr.lewon.dofus.bot.core.fighter

interface IDofusFighter {

    fun getBreed(): Int
    fun getFighterId(): Double
    fun getFighterTeamId(): Int
    fun getPlayerType(): PlayerType
    fun isSummon(): Boolean
    fun getSummonerId(): Double
    fun isStaticElement(): Boolean
    fun hasState(state: Int): Boolean

}