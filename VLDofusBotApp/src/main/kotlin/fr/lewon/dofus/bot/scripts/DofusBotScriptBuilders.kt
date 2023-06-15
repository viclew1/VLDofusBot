package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.*
import fr.lewon.dofus.bot.scripts.impl.dev.PrintAllMapInfoScriptBuilder
import fr.lewon.dofus.bot.scripts.impl.dev.TestScriptBuilder

enum class DofusBotScriptBuilders(val builder: DofusBotScriptBuilder) {

    REACH_MAP(ReachMapScriptBuilder),
    EXPLORE_AREA(ExploreAreaScriptBuilder),
    EXPLORE_ALL_ZAAPS(ExploreAllZaapsScriptBuilder),
    UPDATE_METAMOB(UpdateMetamobScriptBuilder),
    TREASURE_HUNT_EXECUTE(ExecuteTreasureHuntScriptBuilder),
    FIGHT_ARENA(FightArenaScriptBuilder),
    FIGHT_DUNGEON(FightDungeonScriptBuilder),
    SMITH_MAGIC(SmithMagicScriptBuilder),
    RAISE_MOUNTS(RaiseMountsScriptBuilder),
    PRINT_ALL_MAP_INFO(PrintAllMapInfoScriptBuilder),
    TEST(TestScriptBuilder);

}