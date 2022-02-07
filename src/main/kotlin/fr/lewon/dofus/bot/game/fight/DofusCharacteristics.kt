package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.CharacteristicManager
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.CharacterCharacteristic
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicDetailed
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.impl.CharacterCharacteristicValue

enum class DofusCharacteristics(val id: Int) {

    LIFE_POINTS(0),
    ACTION_POINTS(1),
    STATS_POINTS(3),
    SPELLS_POINTS(4),
    LEVEL(5),
    STRENGTH(10),
    VITALITY(11),
    WISDOM(12),
    CHANCE(13),
    AGILITY(14),
    INTELLIGENCE(15),
    ALL_DAMAGES_BONUS(16),
    CRITICAL_HIT(18),
    RANGE(19),
    MAGICAL_REDUCTION(20),
    PHYSICAL_REDUCTION(21),
    EXPERIENCE_BOOST(22),
    MOVEMENT_POINTS(23),
    INVISIBILITY(24),
    DAMAGES_BONUS_PERCENT(25),
    SUMMONABLE_CREATURES_BOOST(26),
    DODGE_PA_LOST_PROBABILITY(27),
    DODGE_PM_LOST_PROBABILITY(28),
    ENERGY_POINTS(29),
    ALIGNEMENT_VALUE(30),
    WEAPON_DAMAGES_BONUS_PERCENT(31),
    PHYSICAL_DAMAGES(32),
    EARTH_ELEMENT_RESIST_PERCENT(33),
    FIRE_ELEMENT_RESIST_PERCENT(34),
    WATER_ELEMENT_RESIST_PERCENT(35),
    AIR_ELEMENT_RESIST_PERCENT(36),
    NEUTRAL_ELEMENT_RESIST_PERCENT(37),
    CRITICAL_MISS(39),
    WEIGHT(40),
    ALIGNEMENT_SIDE(43),
    INITIATIVE(44),
    SHOP_PERCENT_REDUCTION(45),
    ALIGNEMENT_RANK(46),
    MAX_ENERGY_POINTS(47),
    PROSPECTING(48),
    HEAL_BONUS(49),
    REFLECT(50),
    ENERGY_LOOSE(51),
    HONOUR_POINTS(52),
    EARTH_ELEMENT_REDUCTION(54),
    FIRE_ELEMENT_REDUCTION(55),
    WATER_ELEMENT_REDUCTION(56),
    AIR_ELEMENT_REDUCTION(57),
    NEUTRAL_ELEMENT_REDUCTION(58),
    TRAP_BONUS_PERCENT(69),
    TRAP_BONUS(70),
    SOUL_CAPTURE_BONUS(72),
    RIDE_XP_BONUS(73),
    CONFUSION(74),
    PERMANENT_DAMAGE_PERCENT(75),
    UNLUCKY(76),
    MAXIMIZE_ROLL(77),
    TACKLE_EVADE(78),
    TACKLE_BLOCK(79),
    ALLIANCE_AUTO_AGRESS_RANGE(80),
    ALLIANCE_AUTO_AGRESS_RESIST(81),
    PA_ATTACK(82),
    PM_ATTACK(83),
    PUSH_DAMAGE_BONUS(84),
    PUSH_DAMAGE_REDUCTION(85),
    CRITICAL_DAMAGE_BONUS(86),
    CRITICAL_DAMAGE_REDUCTION(87),
    EARTH_DAMAGE_BONUS(88),
    FIRE_DAMAGE_BONUS(89),
    WATER_DAMAGE_BONUS(90),
    AIR_DAMAGE_BONUS(91),
    NEUTRAL_DAMAGE_BONUS(92),
    MAX_BOMB(93),
    BOMB_COMBO(94),
    MAX_LIFE_POINTS(95),
    SHIELD(96),
    CUR_LIFE(97),
    SPELL_PERCENT_DAMAGES(98),
    EXTRA_SCALE(99),
    PASS_TURN(100),
    PERCENT_RESIST(101),
    WEAPON_POWER(103),
    INCOMING_PERCENT_DAMAGE_MULTIPLICATOR(104),
    INCOMING_PERCENT_HEAL_MULTIPLICATOR(105),
    GLYPH_POWER(106),
    DEALT_DAMAGES_MULTIPLICATOR(107),
    STOP_XP(108),
    HUNTER(109),
    RUNE_POWER(110),
    RANGED_DAMAGE_DONE_PERCENT(120),
    RANGED_DAMAGE_RECEIVED_PERCENT(121),
    WEAPON_DAMAGE_DONE_PERCENT(122),
    SPELL_DAMAGE_DONE_PERCENT(123),
    MELEE_DAMAGE_RECEIVED_PERCENT(124),
    MELEE_DAMAGE_DONE_PERCENT(125),
    AGILITY_INITIAL_PERCENT(126),
    STRENGTH_INITIAL_PERCENT(127),
    CHANCE_INITIAL_PERCENT(128),
    INTELLIGENCE_INITIAL_PERCENT(129),
    VITALITY_INITIAL_PERCENT(130),
    WISDOM_INITIAL_PERCENT(131),
    TACKLE_BLOCK_INITIAL_PERCENT(132),
    TACKLE_EVADE_INITIAL_PERCENT(133),
    ACTION_POINTS_INITIAL_PERCENT(134),
    MOVEMENT_POINTS_INITIAL_PERCENT(135),
    PA_ATTACK_INITIAL_PERCENT(136),
    PM_ATTACK_INITIAL_PERCENT(137),
    DODGE_PA_LOST_PROBABILITY_INITIAL_PERCENT(138),
    DODGE_PM_LOST_PROBABILITY_INITIAL_PERCENT(139),
    EXTRA_SCALE_PERCENT(140),
    SPELL_DAMAGE_RECEIVED_PERCENT(141),
    WEAPON_DAMAGE_RECEIVED_PERCENT(142),
    MAX_ACTION_POINTS(144),
    MAX_MOVEMENT_POINTS(145);

    private val dofusCharacteristic = CharacteristicManager.getCharacteristic(id)
        ?: error("No characteristic for id : $id")

    fun getValue(fighter: Fighter, defaultValue: Int = 0): Int {
        val charac = fighter.statsById[dofusCharacteristic.id]
            ?: fighter.baseStatsById[dofusCharacteristic.id]
        return getCharacValue(charac, defaultValue)
    }

    private fun getCharacValue(charac: CharacterCharacteristic?, defaultValue: Int = 0): Int {
        charac ?: return defaultValue
        return when (charac) {
            is CharacterCharacteristicDetailed -> charac.base + charac.additional + charac.contextModif + charac.objectsAndMountBonus + charac.alignGiftBonus
            is CharacterCharacteristicValue -> charac.total
            else -> error("Untreated characteristic type : ${charac::class.java.typeName}")
        }
    }

    companion object {
        fun fromId(id: Int): DofusCharacteristics? {
            return values().firstOrNull { it.dofusCharacteristic.id == id }
        }
    }

}