package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DTBConfigManager
import fr.lewon.dofus.bot.util.Directions
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil

object FindArchimonsterScript : DofusBotScript("Find archimonster") {

    private val stopOnFirstFoundParameter = DofusBotScriptParameter(
        "Stop on first found",
        "If true, the script stops once an archimonster is found, else, it will explore everything it cans and give a list of the found ones",
        "true",
        DofusBotScriptParameterType.BOOLEAN
    )

    private val movePosDiffByDir = mapOf<Directions, Pair<Int, Int>>(
        Pair(Directions.LEFT, Pair(-1, 0)),
        Pair(Directions.RIGHT, Pair(1, 0)),
        Pair(Directions.BOTTOM, Pair(0, 1)),
        Pair(Directions.TOP, Pair(0, -1))
    )

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(
            stopOnFirstFoundParameter
        )
    }

    override fun getStats(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Go through all accessible maps in order to find an archimonster"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val archiHere = archiHere(controller) || floodExploration(getLocation(), null, controller, logItem)
        controller.log("Archi here : $archiHere", logItem)
    }

    private fun floodExploration(
        location: Pair<Int, Int>,
        lastDir: Directions?,
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        treated: ArrayList<Pair<Int, Int>> = ArrayList(),
        processedMoves: ArrayList<Pair<Pair<Int, Int>, Directions>> = ArrayList()
    ): Boolean {
        if (archiHere(controller)) {
            return true
        }
        if (treated.contains(location)) {
            return false
        }
        treated.add(location)
        for (dir in Directions.values()) {
            if (dir == lastDir?.getReverseDir()) {
                continue
            }

            if (movePosDiffByDir[dir]?.let {
                    val newPos = Pair(location.first + it.first, location.second + it.second)
                    treated.contains(newPos)
                } == true) {
                continue
            }

            val move = Pair(location, dir)
            val locationKey = "${location.first}_${location.second}"
            val isClassicMoveImpossible =
                DTBConfigManager.config.registeredImpossibleDirectionsByMap[locationKey]?.contains(dir) ?: false
            val isMoveRedefined =
                DTBConfigManager.config.registeredMoveLocationsByMap[locationKey]?.contains(dir) ?: false
            if (!isMoveRedefined && isClassicMoveImpossible || processedMoves.contains(move)) {
                continue
            }
            processedMoves.add(move)

            val newPos = try {
                dir.buildMoveTask(controller, logItem).run()
            } catch (e: Exception) {
                DTBConfigManager.editConfig { config ->
                    config.registeredImpossibleDirectionsByMap.putIfAbsent(locationKey, ArrayList())
                    config.registeredImpossibleDirectionsByMap[locationKey]?.add(dir)
                }
                continue
            }
            if (floodExploration(newPos, dir, controller, logItem, treated, processedMoves)) {
                return true
            }

            try {
                dir.getReverseDir().buildMoveTask(controller, logItem).run()
            } catch (e: Exception) {
                continue
            }
            if (archiHere(controller)) {
                return true
            }
        }
        return false
    }

    private fun archiHere(controller: DofusTreasureBotGUIController): Boolean {
        return GameInfoUtil.patternFound(controller.captureGameImage(), 0.5, true, DofusImages.ARCHI_MONSTER.path)
    }

}