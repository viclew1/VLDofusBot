package fr.lewon.dofus.bot.core.d2o.managers.map

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.maps.DofusHint

object HintManager : VldbManager {

    private val hintsByGfx = HashMap<HintType, ArrayList<DofusHint>>()

    override fun initManager() {
        hintsByGfx.clear()
        for (hint in D2OUtil.getObjects("Hints")) {
            val gfx = hint["gfx"].toString().toInt()
            val hintType = HintType.fromGfx(gfx)
            if (hintType != null) {
                val id = hint["id"].toString().toInt()
                val outdoor = hint["outdoor"].toString().toBoolean()
                val worldMapId = hint["worldMapId"].toString().toInt()
                val worldMap = WorldMapManager.getWorldMap(worldMapId)
                val level = hint["level"].toString().toInt()
                val x = hint["x"].toString().toInt()
                val y = hint["y"].toString().toInt()
                val nameId = hint["nameId"].toString().toInt()
                val name = I18NUtil.getLabel(nameId) ?: "[INVALID_HINT_NAME]"
                val mapId = hint["mapId"].toString().toDouble()
                val map = MapManager.getDofusMap(mapId)
                val categoryId = hint["categoryId"].toString().toInt()
                hintsByGfx.computeIfAbsent(hintType) { ArrayList() }
                    .add(DofusHint(id, outdoor, worldMap, level, gfx, x, y, name, map, categoryId))
            }
        }
    }

    override fun getNeededManagers(): List<VldbManager> = listOf(MapManager)

    fun getHints(hintType: HintType): List<DofusHint> = hintsByGfx[hintType] ?: emptyList()

    enum class HintType(val gfx: Int) {

        ZAAP(410),
        DUNGEON(422);

        companion object {
            fun fromGfx(gfx: Int): HintType? {
                return values().firstOrNull { it.gfx == gfx }
            }
        }
    }
}