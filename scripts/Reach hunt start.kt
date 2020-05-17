var huntStartImg = getSubImage("start_hunt.png") ?: throw Exception("No hunt start found")
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