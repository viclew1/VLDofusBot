package fr.lewon.dofus.bot.gui.main.metamob.filter

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.ChoiceParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.IntParameter
import fr.lewon.dofus.bot.scripts.parameters.impl.StringParameter
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterType

val MonsterFilters = listOf(
    MonsterFilter(
        StringParameter(
            key = "Name",
            description = "Monster name",
            defaultValue = "",
        )
    ) { value, monster ->
        StringUtil.removeAccents(monster.name).lowercase().contains(StringUtil.removeAccents(value).lowercase())
    },
    MonsterFilter(
        ChoiceParameter(
            key = "Type",
            description = "Monsters type",
            defaultValue = MetamobMonsterType.ANY,
            getAvailableValues = { MetamobMonsterType.entries },
            itemValueToString = { it.displayLabel },
            stringToItemValue = { MetamobMonsterType.fromDisplayLabel(it) }
        )
    ) { value, monster ->
        value == MetamobMonsterType.ANY || monster.type == value
    },
    MonsterFilter(
        ChoiceParameter(
            key = "Search status",
            description = "Monsters search status",
            defaultValue = SearchedParameterValues.ANY,
            getAvailableValues = { SearchedParameterValues.entries },
            itemValueToString = { it.label },
            stringToItemValue = { SearchedParameterValues.fromLabel(it) }
        )
    ) { value, monster ->
        value.monsterMatchesFun(monster)
    },
    MonsterFilter(
        ChoiceParameter(
            key = "Owned",
            description = "Owned monsters",
            defaultValue = OwnedParameterValues.ANY,
            getAvailableValues = { OwnedParameterValues.entries },
            itemValueToString = { it.label },
            stringToItemValue = { OwnedParameterValues.fromLabel(it) }
        )
    ) { value, monster ->
        value.monsterMatchesFun(monster)
    },
    MonsterFilter(
        IntParameter(
            key = "Minimal amount",
            description = "Minimal amount owned",
            defaultValue = 0,
        )
    ) { value, monster ->
        monster.amount >= (value)
    }
)

data class MonsterFilter<T>(
    val parameter: DofusBotParameter<T>,
    val isMonsterValid: (T, MetamobMonster) -> Boolean,
)