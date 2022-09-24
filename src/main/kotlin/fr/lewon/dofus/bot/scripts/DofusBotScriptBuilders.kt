package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.*
import fr.lewon.dofus.bot.scripts.impl.dev.*

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
    REGISTER_HINT_GFX(RegisterHintGfxScriptBuilder),
    REMOVE_HINT_GFX(RemoveHintGfxScriptBuilder),
    PRINT_ALL_MAP_INFO(PrintAllMapInfoScriptBuilder),
    SHOW_D20_CONTENT(ReadD2OFileScriptBuilder),
    READ_SPELL_INFO(ReadSpellInfoScriptBuilder),
    READ_LABEL(ReadLabelScriptBuilder),
    TEST(TestScriptBuilder);

}