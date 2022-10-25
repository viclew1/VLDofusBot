package fr.lewon.dofus.bot.model.dungeon

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager

enum class Dungeon(
    mapId: Double,
    val enterNpcId: Int,
    val enterDialogIds: List<Int>,
    val exitNpcId: Int = enterNpcId,
    val exitDialogIds: List<Int>
) {
    BOUFTOU(120063489.0, 173, listOf(10529, 20330), exitDialogIds = listOf(10533)),
    CHAMPS(192937992.0, 780, listOf(2802, 20904), exitDialogIds = listOf(15920)),
    ENSABLE(
        190056961.0,
        798,
        listOf(2936, 2937, 2938, 2939, 2940, 2941, 2942, 2943, 2944, 20326),
        exitDialogIds = listOf(2945, 2946)
    ),
    DRAEGNERYS(84411397.0, 4946, listOf(47029, 47027), 4947, listOf(47041)),
    ;

    val map = MapManager.getDofusMap(mapId)

}