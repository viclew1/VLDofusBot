package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType

enum class MonsterFilter(
    val parameter: DofusBotParameter,
    val isMonsterValidFun: (String, MetamobMonster) -> Boolean
) {
    NAME(
        DofusBotParameter(
            key = "Name",
            description = "Monster name",
            defaultValue = "",
            type = DofusBotParameterType.STRING,
        ),
        { value, monster ->
            StringUtil.removeAccents(monster.name).lowercase().contains(StringUtil.removeAccents(value).lowercase())
        }
    ),
    TYPE(
        DofusBotParameter(
            key = "Type",
            description = "Monsters type",
            defaultValue = MetamobMonsterType.ANY.displayLabel,
            type = DofusBotParameterType.CHOICE,
            possibleValues = MetamobMonsterType.values().map { it.displayLabel }
        ),
        { value, monster ->
            val type = MetamobMonsterType.fromDisplayLabel(value)
            type == MetamobMonsterType.ANY || monster.type == type
        }
    ),
    SEARCH_STATUS(
        DofusBotParameter(
            key = "Search status",
            description = "Monsters search status",
            defaultValue = SearchedParameterValues.ANY.label,
            type = DofusBotParameterType.CHOICE,
            possibleValues = SearchedParameterValues.values().map { it.label }
        ),
        { value, monster ->
            SearchedParameterValues.fromLabel(value).monsterMatchesFun(monster)
        }
    ),
    OWNED(
        DofusBotParameter(
            key = "Owned",
            description = "Owned monsters",
            defaultValue = OwnedParameterValues.ANY.label,
            type = DofusBotParameterType.CHOICE,
            possibleValues = OwnedParameterValues.values().map { it.label }
        ),
        { value, monster ->
            OwnedParameterValues.fromLabel(value).monsterMatchesFun(monster)
        }
    ),
    MINIMAL_AMOUNT(
        DofusBotParameter(
            key = "Minimal amount",
            description = "Minimal amount owned",
            defaultValue = "0",
            type = DofusBotParameterType.INTEGER,
        ),
        { value, monster ->
            monster.amount >= (value.toIntOrNull() ?: 0)
        }
    )
    ;

    fun isMonsterValid(filterValue: String, monster: MetamobMonster): Boolean {
        return isMonsterValidFun(filterValue, monster)
    }
}