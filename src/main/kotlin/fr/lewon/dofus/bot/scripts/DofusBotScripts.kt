package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.*
import fr.lewon.dofus.bot.scripts.impl.dev.*

enum class DofusBotScripts(val buildScriptFun: () -> DofusBotScript) {

    REACH_MAP({ ReachMapScript() }),
    EXPLORE_AREA({ ExploreAreaScript() }),
    EXPLORE_ALL_ZAAPS({ ExploreAllZaapsScript() }),
    UPDATE_METAMOB({ UpdateMetamobScript() }),
    TREASURE_HUNT_EXECUTE({ ExecuteTreasureHuntScript() }),
    FIGHT_ARENA({ FightArenaScript() }),
    FIGHT_DUNGEON({ FightDungeonScript() }),
    SMITH_MAGIC({ SmithMagicScript() }),
    RAISE_MOUNTS({ RaiseMountsScript() }),
    REGISTER_HINT_GFX({ RegisterHintGfxScript() }),
    REMOVE_HINT_GFX({ RemoveHintGfxScript() }),
    PRINT_ALL_MAP_INFO({ PrintAllMapInfoScript() }),
    SHOW_D20_CONTENT({ ReadD2OFileScript() }),
    READ_SPELL_INFO({ ReadSpellInfoScript() }),
    READ_LABEL({ ReadLabelScript() }),
    TEST({ TestScript() });

    fun buildScript(): DofusBotScript {
        return buildScriptFun()
    }

}