package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.*

enum class DofusBotScripts(val buildScriptFun: () -> DofusBotScript) {

    REACH_MAP({ ReachMapScript() }),
    INIT_ALL({ InitAllScript() }),
    EXPLORE_AREA({ ExploreAreaScript() }),
    EXPLORE_ALL_ZAAPS({ ExploreAllZaapsScript() }),
    TREASURE_HUNT_EXECUTE({ ExecuteTreasureHuntScript() }),
    FIGHT_ARENA({ FightArenaScript() }),
    FIGHT_DUNGEON({ FightDungeonScript() }),
    SMITH_MAGIC({ SmithMagicScript() }),
    RAISE_MOUNTS({ RaiseMountsScript() }),
    PRINT_ALL_MAP_INFO({ PrintAllMapInfoScript() }),
    SHOW_D20_CONTENT({ ReadD2OFileScript() }),
    READ_SPELL_INFO({ ReadSpellInfoScript() }),
    READ_LABEL({ ReadLabelScript() }),
    TEST({ TestScript() });

    fun buildScript(): DofusBotScript {
        return buildScriptFun()
    }

}