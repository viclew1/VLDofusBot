package fr.lewon.dofus.bot.scripts

import fr.lewon.dofus.bot.scripts.impl.MultipleTreasureHuntScript
import fr.lewon.dofus.bot.scripts.impl.TestScript

enum class DofusBotScripts(val script: DofusBotScript) {

    TREASURE_HUNT_EXECUTE(MultipleTreasureHuntScript),
    TEST_FIGHT(TestScript)

}