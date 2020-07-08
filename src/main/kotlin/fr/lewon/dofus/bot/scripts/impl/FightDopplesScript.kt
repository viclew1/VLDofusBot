package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.complex.ReachMapTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem

object FightDopplesScript : DofusBotScript("Fight dopples") {

    private var sramFought = "-"
    private var huppermageFought = "-"
    private var sacrierFought = "-"
    private var enutrofFought = "-"
    private var ecaflipFought = "-"
    private var masqueraiderFought = "-"
    private var fecaFought = "-"
    private var ouginakFought = "-"
    private var foggernautsFought = "-"
    private var osamodasFought = "-"
    private var eniripsaFought = "-"
    private var pandawaFought = "-"
    private var eliotropeFought = "-"
    private var xelorFought = "-"
    private var rogueFought = "-"
    private var iopFought = "-"
    private var craFought = "-"
    private var sadidaFought = "-"

    override fun getParameters(): List<DofusBotScriptParameter> {
        return emptyList()
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf(
            Pair("Sram fought", sramFought),
            Pair("Huppermage fought", huppermageFought),
            Pair("Sacrier fought", sacrierFought),
            Pair("Enutrof fought", enutrofFought),
            Pair("Ecaflip fought", ecaflipFought),
            Pair("Masqueraider fought", masqueraiderFought),
            Pair("Feca fought", fecaFought),
            Pair("Ouginak fought", ouginakFought),
            Pair("Foggernauts fought", foggernautsFought),
            Pair("Osamodas fought", osamodasFought),
            Pair("Eniripsa fought", eniripsaFought),
            Pair("Pandawa fought", pandawaFought),
            Pair("Eliotrope fought", eliotropeFought),
            Pair("Xelor fought", xelorFought),
            Pair("Rogue fought", rogueFought),
            Pair("Iop fought", iopFought),
            Pair("Cra fought", craFought),
            Pair("Sadida fought", sadidaFought)
        )
    }

    override fun getDescription(): String {
        return "Fight every one of the 18 dopples"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        sramFought = "-"
        huppermageFought = "-"
        sacrierFought = "-"
        enutrofFought = "-"
        ecaflipFought = "-"
        masqueraiderFought = "-"
        fecaFought = "-"
        ouginakFought = "-"
        foggernautsFought = "-"
        osamodasFought = "-"
        eniripsaFought = "-"
        pandawaFought = "-"
        eliotropeFought = "-"
        xelorFought = "-"
        rogueFought = "-"
        iopFought = "-"
        craFought = "-"
        sadidaFought = "-"

        // Sram temple
        reachDestination(-4, 0)
        execTimeoutOpe({ clickPoint(1109, 426) }, { imgFound("dopples/sram_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(459, 238) }, { imgFound("dopples/sram_temple_dopple_room.png") })
        clickPoint(899, 421)
        fightDopple { sramFought }

        // Huppermage temple
        ReachMapTask(controller, logItem, 39, -21).run()
        execTimeoutOpe({ clickPoint(1511, 707) }, { imgFound("dopples/huppermage_temple_dopple_room.png") })
        clickPoint(902, 561)
        fightDopple { huppermageFought }

        // Sacrier temple
        ReachMapTask(controller, logItem, -3, -4).run()
        execTimeoutOpe({ clickPoint(723, 310) }, { imgFound("dopples/sacrier_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(612, 131) }, { imgFound("dopples/sacrier_temple_main_room.png") })
        execTimeoutOpe({ clickPoint(1437, 203) }, { imgFound("dopples/sacrier_temple_dopple_room.png") })
        clickPoint(1121, 429)
        fightDopple { sacrierFought }

        // Enutrof temple
        ReachMapTask(controller, logItem, -1, -4).run()
        execTimeoutOpe({ clickPoint(1156, 535) }, { imgFound("dopples/enutrof_temple_main_room.png") })
        execTimeoutOpe({ clickPoint(570, 421) }, { imgFound("dopples/enutrof_temple_dopple_room.png") })
        clickPoint(1123, 484)
        fightDopple { enutrofFought }

        // Ecaflip temple
        ReachMapTask(controller, logItem, 1, -5).run()
        execTimeoutOpe({ clickPoint(1040, 382) }, { imgFound("dopples/ecaflip_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1227, 404) }, { imgFound("dopples/ecaflip_temple_main_room.png") })
        execTimeoutOpe({ clickPoint(997, 293) }, { imgFound("dopples/ecaflip_temple_dopple_room.png") })
        clickPoint(1424, 397)
        fightDopple { ecaflipFought }

        // Masqueraider temple
        ReachMapTask(controller, logItem, 1, -8).run()
        execTimeoutOpe({ clickPoint(672, 112) }, { imgFound("dopples/masqueraider_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1359, 50) }, { imgFound("dopples/masqueraider_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(470, 138) }, { imgFound("dopples/masqueraider_temple_room_3.png") })
        execTimeoutOpe({ clickPoint(970, 396) }, { imgFound("dopples/masqueraider_temple_room_4.png") })
        execTimeoutOpe({ clickPoint(1297, 383) }, { imgFound("dopples/masqueraider_temple_dopple_room.png") })
        clickPoint(1172, 445)
        fightDopple { masqueraiderFought }

        // Feca temple
        reachDestination(12, 5)
        execTimeoutOpe({ clickPoint(1230, 294) }, { imgFound("dopples/feca_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1343, 199) }, { imgFound("dopples/feca_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(438, 28) }, { imgFound("dopples/feca_temple_room_3.png") })
        execTimeoutOpe({ clickPoint(419, 919) }, { imgFound("dopples/feca_temple_dopple_room.png") })
        clickPoint(930, 474)
        fightDopple { fecaFought }

        // Ouginak temple
        ReachMapTask(controller, logItem, 12, 3).run()
        execTimeoutOpe({ clickPoint(1044, 500) }, { imgFound("dopples/ouginak_boat.png", 0.8) })
        execTimeoutOpe({ click("dopples/ouginak_boat.png") }, { getLocation() == Pair(7, -66) })
        moveRight()
        moveTop()
        execTimeoutOpe({ clickPoint(1216, 586) }, { imgFound("dopples/ouginak_temple_main_room.png") })
        execTimeoutOpe({ clickPoint(1418, 407) }, { imgFound("dopples/ouginak_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(585, 341) }, { imgFound("dopples/ouginak_temple_dopple_room.png") })
        clickPoint(920, 360)
        fightDopple { ouginakFought }

        // Foggernauts temple
        reachDestination(9, 1)
        ReachMapTask(controller, logItem, 26, 22).run()
        execTimeoutOpe({ clickPoint(1131, 444) }, { imgFound("dopples/foggernauts_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1011, 586) }, { imgFound("dopples/foggernauts_temple_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(1239, 820) }, { imgFound("dopples/foggernauts_temple_dopple_room.png") })
        clickPoint(724, 608)
        fightDopple { foggernautsFought }

        // Osamodas temple
        reachDestination(8, 2)
        execTimeoutOpe({ clickPoint(1154, 520) }, { imgFound("dopples/osamodas_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(682, 220) }, { imgFound("dopples/osamodas_temple_dopple_room.png") })
        clickPoint(1019, 517)
        fightDopple { osamodasFought }

        // Eniripsa temple
        ReachMapTask(controller, logItem, 7, 1).run()
        execTimeoutOpe({ clickPoint(945, 563) }, { imgFound("dopples/eniripsa_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1294, 485) }, { imgFound("dopples/eniripsa_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(1187, 257) }, { imgFound("dopples/eniripsa_temple_dopple_room.png") })
        clickPoint(1214, 439)
        fightDopple { eniripsaFought }

        // Pandawa temple
        reachDestination(4, -2)
        execTimeoutOpe({ clickPoint(653, 438) }, { imgFound("dopples/pandawa_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1330, 171) }, { imgFound("dopples/pandawa_temple_dopple_room.png") })
        clickPoint(1046, 518)
        fightDopple { pandawaFought }

        // Eliotrope temple
        ReachMapTask(controller, logItem, 16, 33).run()
        moveRight()
        moveLeft()
        clickPoint(892, 405)
        fightDopple { eliotropeFought }

        // Xelor temple
        reachDestination(3, 1)
        execTimeoutOpe({ clickPoint(1256, 486) }, { imgFound("dopples/xelor_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1473, 154) }, { imgFound("dopples/xelor_temple_dopple_room.png") })
        clickPoint(1120, 386)
        fightDopple { xelorFought }

        // Rogue temple
        ReachMapTask(controller, logItem, 3, 3).run()
        execTimeoutOpe({ clickPoint(1264, 192) }, { imgFound("rogue_temple_boat.png") })
        execTimeoutOpe({ clickPoint(1263, 505) }, { imgFound("dopples/rogue_temple_boat_2.png", 0.8) })
        execTimeoutOpe({ click("dopples/rogue_temple_boat_2.png") }, { getLocation() == Pair(10, 16) })
        execTimeoutOpe({ clickPoint(1033, 118) }, { imgFound("dopples/rogue_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(496, 463) }, { imgFound("dopples/rogue_temple_room_2.png") })
        execTimeoutOpe({ clickPoint(682, 303) }, { imgFound("dopples/rogue_temple_room_3.png") })
        execTimeoutOpe({ clickPoint(584, 402) }, { imgFound("dopples/rogue_temple_dopple_room.png") })
        clickPoint(1256, 388)
        fightDopple { rogueFought }

        // Cra temple
        reachDestination(0, 3)
        execTimeoutOpe({ clickPoint(1164, 559) }, { imgFound("dopples/cra_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1157, 212) }, { imgFound("dopples/cra_temple_dopple_room.png") })
        clickPoint(1086, 572)
        fightDopple { craFought }

        // Iop temple
        ReachMapTask(controller, logItem, 1, 3).run()
        execTimeoutOpe({ clickPoint(1010, 360) }, { imgFound("dopples/iop_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(1413, 593) }, { imgFound("dopples/iop_temple_dopple_room.png") })
        clickPoint(1084, 575)
        fightDopple { iopFought }

        // Sadida temple
        reachDestination(-1, 9)
        execTimeoutOpe({ clickPoint(971, 418) }, { imgFound("dopples/sadida_temple.png", 0.9) })
        execTimeoutOpe({ clickPoint(748, 340) }, { imgFound("dopples/sadida_temple_dopple_room.png") })
        clickPoint(893, 478)
        fightDopple { sadidaFought }
    }

    private fun fightDopple(statUpdater: (String) -> Unit) {
        execTimeoutOpe({}, { imgFound("dopples/train_with_dopple.png", 0.8) })
        clickChain(listOf("dopples/train_with_dopple.png", "dopples/begin_fight_dopple.png"))
        statUpdater.invoke("...")
        runScript(FightScript)
        statUpdater.invoke("OK")
    }
}