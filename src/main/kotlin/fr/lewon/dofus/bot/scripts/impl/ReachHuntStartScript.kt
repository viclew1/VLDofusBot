package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter

object ReachHuntStartScript : DofusBotScript("Reach hunt start") {

    override fun getParameters(): List<DofusBotScriptParameter> {
        return emptyList()
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Reaches the hunt start using the autopilot mount (if you don't have one, it won't work for now)"
    }

    override fun doExecute(parameters: Map<String, DofusBotScriptParameter>) {
        val huntStartBounds = imgBounds("start_hunt.png") ?: throw Exception("No hunt start found")
        huntStartBounds.width = 300
        val huntStartImg = getSubImage(huntStartBounds)
        for (i in 5..8) {
            var treatedHuntStartImg = resize(huntStartImg, i)
            treatedHuntStartImg = keepDark(treatedHuntStartImg, true)
            val lines = getLines(treatedHuntStartImg)
            println(lines[0])
            val startPosStr = Regex("(-?[0-9]+[\b]*,[\b]*-?[0-9]+)").find(lines[0])
                ?.destructured
                ?.component1()
                ?: continue

            val startCoordinatesStrSplit = startPosStr.split(",")
            val x = startCoordinatesStrSplit[0].trim().toInt()
            val y = startCoordinatesStrSplit[1].trim().toInt()
            val startCoordinates = Pair(x, y)
            log("Hunt start : [${startCoordinates.first}, ${startCoordinates.second}]")

            if (getLocation() == startCoordinates) {
                log("Already arrived")
                return
            }

            reachDestination(startCoordinates.first, startCoordinates.second)
            return
        }
        throw Exception("No pos found")
    }

}