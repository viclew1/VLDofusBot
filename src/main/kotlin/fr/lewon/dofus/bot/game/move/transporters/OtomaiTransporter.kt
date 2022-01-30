package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

enum class OtomaiTransporter(idMap: Double, private val optionIndex: Int) : ITransporter {

    PLAINES_HERBEUSES(159766.0, 0),
    VILLAGE_COTIER(156174.0, 1),
    VILLAGE_DES_ELEVEURS(160260.0, 2);

    private val map = MapManager.getDofusMap(idMap)
    private val transporterMap = MapManager.getDofusMap(20973058.0)

    override fun getTransporterMap(): DofusMap {
        return transporterMap
    }

    override fun getMap(): DofusMap {
        return map
    }

    override fun getNpcId(): Int {
        return 935
    }

    override fun getOptionIndex(): Int {
        return optionIndex
    }

}