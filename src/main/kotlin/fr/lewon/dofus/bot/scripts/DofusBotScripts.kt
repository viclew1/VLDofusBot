package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.InitAllScript
import fr.lewon.dofus.bot.scripts.impl.MultipleTreasureHuntScript
import fr.lewon.dofus.bot.scripts.impl.TestScript
import fr.lewon.dofus.bot.scripts.impl.TravelScript

enum class DofusBotScripts(val script: DofusBotScript) {

    TREASURE_HUNT_EXECUTE(MultipleTreasureHuntScript),
    INIT_ALL(InitAllScript),
    TRAVEL(TravelScript),
    TEST(TestScript)

}